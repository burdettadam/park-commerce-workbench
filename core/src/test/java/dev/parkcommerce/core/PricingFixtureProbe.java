package dev.parkcommerce.core;

import java.io.*;
import java.time.*;
import java.util.*;
import static dev.parkcommerce.core.Catalog.*;

/** Test-only stdin bridge for independent fixture/oracle comparisons; never wired into Spring. */
public final class PricingFixtureProbe {
    private PricingFixtureProbe() {}
    public static void main(String[] args) throws IOException {
        try (var input = new BufferedReader(new InputStreamReader(System.in, java.nio.charset.StandardCharsets.UTF_8))) {
            String row; int count=0;
            while ((row=input.readLine())!=null) {
                if (++count>2000 || row.length()>300) throw new IllegalArgumentException("Fixture input limit");
                String[] values=row.split("\\t",-1);
                if(values.length!=7 || !values[0].matches("[a-z0-9-]{1,64}")) throw new IllegalArgumentException("Malformed fixture row");
                long adult=Long.parseLong(values[1]), child=Long.parseLong(values[2]);
                int adults=Integer.parseInt(values[3]), children=Integer.parseInt(values[4]);
                int threshold=Integer.parseInt(values[5]), basisPoints=Integer.parseInt(values[6]);
                var day=LocalDate.of(2030,10,31);
                var product=new Product("fixture-night",1,"Fixture Night",Kind.EVENT,EnumSet.allOf(Variant.class));
                var store=new MemoryStores.PricingStore();
                var service=new Pricing.Service(store,(id,revision)->product,
                    Clock.fixed(Instant.parse("2030-10-01T12:00:00Z"),ZoneOffset.UTC),ZoneId.of("America/New_York"));
                var book=new Pricing.PriceBook(product.id(),1,1,List.of(new Pricing.Rate(Variant.ADULT,day,day,new Money(adult)),
                    new Pricing.Rate(Variant.CHILD,day,day,new Money(child))),threshold==0?null:new Pricing.Offer("fixture-offer",threshold,basisPoints));
                var request=new Pricing.Request(product.id(),day,adults,children);
                var preview=service.preview(new Pricing.PreviewRequest(book,request));service.publish(book);var quote=service.create(request,"fixture-guest");
                // Only controlled identifiers/enums, integer text and a fixed offer string reach this test serializer.
                System.out.printf(Locale.ROOT,"{\"id\":\"%s\",\"preview\":%s,\"quote\":%s}%n", values[0],
                    amounts(preview.subtotal(),preview.discount(),preview.total(),preview.lines(),preview.appliedOffer()),
                    amounts(quote.subtotal(),quote.discount(),quote.total(),quote.lines(),quote.appliedOffer()));
            }
        }
    }
    private static String amounts(Money subtotal,Money discount,Money total,List<Pricing.Line> lines,String offer) {
        var result=new StringJoiner(",","[","]");
        for(var line:lines)result.add(String.format(Locale.ROOT,
            "{\"variant\":\"%s\",\"quantity\":%d,\"unitPrice\":\"%d\",\"subtotal\":\"%d\",\"discount\":\"%d\",\"total\":\"%d\"}",
            line.variant(),line.quantity(),line.unitPrice().cents(),line.subtotal().cents(),line.discount().cents(),line.total().cents()));
        return String.format(Locale.ROOT,"{\"subtotal\":\"%d\",\"discount\":\"%d\",\"total\":\"%d\",\"appliedOffer\":%s,\"lines\":%s}",
            subtotal.cents(),discount.cents(),total.cents(),offer==null?"null":"\"fixture-offer\"",result);
    }
}
