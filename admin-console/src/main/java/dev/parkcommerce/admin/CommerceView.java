package dev.parkcommerce.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.*;
import dev.parkcommerce.core.*;
import java.net.http.HttpClient;
import java.time.*;
import java.util.*;
import org.springframework.core.env.Environment;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.*;

/** Workstation-only administration client. Every write is authorized by its owning service. */
@Route("") @PageTitle("Park Commerce | Employee workbench")
public class CommerceView extends VerticalLayout {
 private final PasswordField token=new PasswordField("Employee demo token");
 private final Pre result=new Pre();
 private final RestClient catalog,pricing,booking;
 public CommerceView(RestClient.Builder builder,Environment env){
  catalog=client(builder,env.getRequiredProperty("park.catalog-url"));
  pricing=client(builder,env.getRequiredProperty("park.pricing-url"));
  booking=client(builder,env.getRequiredProperty("park.booking-url"));
  setMaxWidth("1080px");getStyle().set("margin","auto");setPadding(true);
  add(new H1("Park Commerce Workbench"),new Paragraph("Employee operations · Fictional products · Local development only"));
  token.setWidthFull();token.setAutocomplete(Autocomplete.OFF);
  add(token,new Paragraph("Paste DEMO_EMPLOYEE_TOKEN from your local .env. It stays in this view's memory; do not expose this console to the internet."));
  TextField id=text("Product ID","lantern-evening"),name=text("Product name","Lantern Evening");
  IntegerField revision=number("Catalog revision",1,1,1000000);
  ComboBox<Catalog.Kind> kind=new ComboBox<>("Product kind");kind.setItems(Catalog.Kind.values());kind.setValue(Catalog.Kind.EVENT);
  CheckboxGroup<Catalog.Variant> variants=new CheckboxGroup<>();variants.setLabel("Sellable variants");
  variants.setItems(Catalog.Variant.values());variants.setValue(EnumSet.allOf(Catalog.Variant.class));
  FormLayout product=new FormLayout(id,name,revision,kind,variants);
  IntegerField expectedDraft=number("Expected draft version (0 = new)",0,0,1000000);
  IntegerField publishedBase=number("Reviewed published base (0 = new)",0,0,1000000);
  CatalogEditorial.Draft[] selected={null};
  String[] nextHistoryCursor={null},historyProduct={null};
  java.util.function.Supplier<CatalogEditorial.Content> draftContent=()->
   new CatalogEditorial.Content(name.getValue(),kind.getValue(),variants.getValue());
  Button saveDraft=new Button("Save new draft version",e->action(()->{
   String productId=Failure.id(id.getValue(),"Product ID");
   var draft=post(catalog,"/api/admin/products/"+productId+"/drafts",
    new CatalogEditorial.SaveDraft(expectedDraft.getValue(),publishedBase.getValue(),draftContent.get()),CatalogEditorial.Draft.class);
   selected[0]=draft;expectedDraft.setValue(Math.toIntExact(draft.version()));
   return "SAVED DRAFT "+draft.version()+" — not published. Base published revision "+draft.basePublishedRevision()+
    "\nReview these saved values before publishing:\n"+draft;
  }));
  Button loadDraft=new Button("Load latest draft for review",e->action(()->{
   var draft=get(catalog,"/api/admin/products/"+Failure.id(id.getValue(),"Product ID")+"/drafts/latest",CatalogEditorial.Draft.class);
   selected[0]=draft;expectedDraft.setValue(Math.toIntExact(draft.version()));publishedBase.setValue(Math.toIntExact(draft.basePublishedRevision()));
   name.setValue(draft.content().name());kind.setValue(draft.content().kind());variants.setValue(draft.content().variants());
   return "LOADED DRAFT "+draft.version()+" — not published.\n"+draft+
    "\nIf its published base is stale, review current history and save a new rebased draft.";
  }));
  Button publish=new Button("Publish reviewed saved draft",e->action(()->{
   var saved=selected[0];
   if(saved==null||!saved.productId().equals(id.getValue()))throw new IllegalArgumentException("Load or save a draft for this product first");
   if(!saved.content().equals(draftContent.get())||saved.basePublishedRevision()!=publishedBase.getValue())
    throw new IllegalArgumentException("Form changed. Save and review a new draft before publishing");
   var publication=post(catalog,"/api/admin/products/"+saved.productId()+"/publications",
    new CatalogEditorial.PublishDraft(saved.version(),saved.basePublishedRevision()),CatalogEditorial.Publication.class);
   revision.setValue(Math.toIntExact(publication.product().revision()));publishedBase.setValue(Math.toIntExact(publication.product().revision()));
   selected[0]=null;nextHistoryCursor[0]=null;
   return "PUBLISHED revision "+publication.product().revision()+" from saved draft "+publication.sourceDraftVersion()+
    "\n"+publication+"\nLater drafts, if any, remain unpublished. No price book was changed.";
  }));
  Button history=new Button("Load publication history",e->action(()->{
   String productId=Failure.id(id.getValue(),"Product ID");
   var page=get(catalog,"/api/admin/products/"+productId+"/history?limit=10",CatalogEditorial.HistoryPage.class);
   nextHistoryCursor[0]=page.nextCursor();historyProduct[0]=productId;return historyText(page);
  }));
  Button nextHistory=new Button("Next history page",e->action(()->{
   if(nextHistoryCursor[0]==null||!id.getValue().equals(historyProduct[0]))throw new IllegalArgumentException("Load history for this product first; there may be no next page");
   var page=get(catalog,"/api/admin/products/"+Failure.id(id.getValue(),"Product ID")+"/history?limit=10&cursor="+nextHistoryCursor[0],CatalogEditorial.HistoryPage.class);
   nextHistoryCursor[0]=page.nextCursor();return historyText(page);
  }));
  add(new H2("1. Catalog drafts and publication"),
   new Paragraph("Draft version and published revision are different counters. Saving never changes the guest catalog. Publication uses the exact saved draft shown in the result, not an automatic latest draft."),
   product,new FormLayout(expectedDraft,publishedBase),saveDraft,loadDraft,publish,history,nextHistory);
  IntegerField version=number("Pricing version",1,1,1000000);
  DatePicker from=new DatePicker("Valid from",LocalDate.now(ZoneId.of("America/New_York")));
  DatePicker through=new DatePicker("Valid through",LocalDate.now(ZoneId.of("America/New_York")).plusMonths(1));
  TextField adult=text("Adult price (USD)","100.00"),child=text("Child price (USD)","70.00");
  java.util.function.Supplier<Pricing.PriceBook> priceCandidate=()->new Pricing.PriceBook(
   id.getValue(),version.getValue(),revision.getValue(),List.of(
    new Pricing.Rate(Catalog.Variant.ADULT,from.getValue(),through.getValue(),Money.usd(adult.getValue())),
    new Pricing.Rate(Catalog.Variant.CHILD,from.getValue(),through.getValue(),Money.usd(child.getValue()))),
    new Pricing.Offer("family-four",4,1000));
  Button rates=new Button("Publish price book",e->action(()->
   post(pricing,"/api/admin/price-books",priceCandidate.get(),Pricing.PriceBook.class).toString()));
  add(new H2("2. Dated pricing"),new Paragraph("This first UI exposes one offer: 10% off line subtotals for parties of four or more. The API supports a configurable threshold and percentage."),new FormLayout(version,from,through,adult,child),rates);
  TextField event=text("Event occurrence ID","lantern-evening-date");
  DatePicker visit=new DatePicker("Visit date",LocalDate.now(ZoneId.of("America/New_York")).plusDays(7));
  IntegerField capacity=number("Shared adult/child capacity",20,1,1000000);
  Button inventory=new Button("Create event occurrence",e->action(()->post(booking,"/api/admin/events",
   new Booking.Event(event.getValue(),id.getValue(),visit.getValue(),capacity.getValue()),Booking.Event.class).toString()));
  add(new H2("3. Capacity"),new FormLayout(event,visit,capacity),inventory);
  IntegerField adults=number("Adults",2,0,20),children=number("Children",2,0,20);
  Button preview=new Button("Preview unpublished rates",e->action(()->{
   var input=new Pricing.PreviewRequest(priceCandidate.get(),new Pricing.Request(id.getValue(),visit.getValue(),adults.getValue(),children.getValue()));
   Pricing.Preview calculation=post(pricing,"/api/admin/price-books/preview",input,Pricing.Preview.class);
   return "WHAT-IF ONLY — not a quote or reservation\nCandidate "+calculation.candidateId()+
    "\nSubmitted pricing version "+calculation.candidate().version()+" / catalog revision "+calculation.candidate().catalogRevision()+
    "\nSubtotal $"+calculation.subtotal().amount()+"\nDiscount $"+calculation.discount().amount()+
    "\nTotal $"+calculation.total().amount()+"\nOffer "+Objects.toString(calculation.appliedOffer(),"none")+
    "\n\n"+calculation.lines().stream().map(line->line.quantity()+" "+line.variant()+" @ $"+line.unitPrice().amount()+
     " = $"+line.total().amount()+" after $"+line.discount().amount()+" discount").collect(java.util.stream.Collectors.joining("\n"));
  }));
  add(new H2("4. Preview unpublished pricing"),new Paragraph("Uses the unsaved values in the pricing form above, not the latest published prices. Publish the catalog revision first. No quote, order, or inventory is created. Changing the form changes the candidate; preview again before publication."),new FormLayout(adults,children),preview);
  result.getStyle().set("white-space","pre-wrap");result.setWidthFull();result.getElement().setAttribute("role","status");
  add(new H2("Operation result"),result);
 }
 private static RestClient client(RestClient.Builder builder,String url){
  var factory=new JdkClientHttpRequestFactory(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build());factory.setReadTimeout(Duration.ofSeconds(5));
  return builder.clone().baseUrl(url).requestFactory(factory).build();
 }
 private static String historyText(CatalogEditorial.HistoryPage page){
  return "HISTORY — pinned through published revision "+page.snapshotRevision()+"\n"+
   page.items().stream().map(item->"Revision "+item.product().revision()+": "+item.product().name()+
    " / source draft "+Objects.toString(item.sourceDraftVersion(),"none or unknown")+
    " / audit "+item.auditStatus()+"\n"+Objects.toString(item.audit(),"Legacy actor and timestamp unavailable"))
    .collect(java.util.stream.Collectors.joining("\n\n"))+"\nNext page: "+(page.nextCursor()!=null);
 }
 private <T>T get(RestClient client,String path,Class<T> type){
  if(!token.getValue().matches("[A-Za-z0-9_-]{32,128}"))throw new IllegalArgumentException("Provide your employee demo token first");
  return Objects.requireNonNull(client.get().uri(path).header("Authorization","Bearer "+token.getValue()).retrieve().body(type));
 }
 private <T>T post(RestClient client,String path,Object body,Class<T> type){
  if(!token.getValue().matches("[A-Za-z0-9_-]{32,128}"))throw new IllegalArgumentException("Provide your employee demo token first");
  return Objects.requireNonNull(client.post().uri(path).header("Authorization","Bearer "+token.getValue()).body(body).retrieve().body(type));
 }
 private void action(java.util.concurrent.Callable<String> action){
  try{result.setText(action.call());}
  catch(RestClientResponseException error){result.setText(error.getStatusCode().value()==409?
   "CONFLICT — nothing was automatically rebased or retried. Reload the draft and publication history, review the current base, then save a new draft if needed.":
   "Request rejected (HTTP "+error.getStatusCode().value()+"). Check the revision, input, and employee credential.");}
  catch(RestClientException error){result.setText("Response unavailable; a write may already have succeeded. Inspect the latest draft/history before retrying. Confirm the local services and ports are running.");}
  catch(Exception error){result.setText(error instanceof Failure || error instanceof IllegalArgumentException?error.getMessage():"Operation failed; review local configuration and input.");}
 }
 private static TextField text(String label,String value){var field=new TextField(label);field.setValue(value);return field;}
 private static IntegerField number(String label,int value,int min,int max){var field=new IntegerField(label);field.setMin(min);field.setMax(max);field.setValue(value);field.setStepButtonsVisible(true);return field;}
}
