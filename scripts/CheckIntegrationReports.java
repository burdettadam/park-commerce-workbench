import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;

/** Fail the required integration command when a baseline database suite did not execute. */
class CheckIntegrationReports {
    private static final Map<String, String[]> BASELINE = new LinkedHashMap<>();
    static {
        BASELINE.put("catalog-service", new String[] {"CatalogEditorialIT", "MongoCatalogIT"});
        BASELINE.put("pricing-service", new String[] {"JpaPricingIT", "QuoteContractIT"});
        BASELINE.put("booking-service", new String[] {"AdmissionCheckoutIT", "CheckoutRecoveryIT", "JdbcBookingIT"});
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 0) throw new IllegalArgumentException("No arguments expected");
        var parser = DocumentBuilderFactory.newInstance();
        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        parser.setFeature("http://xml.org/sax/features/external-general-entities", false);
        parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        int suites = 0;
        int tests = 0;
        for (var entry : BASELINE.entrySet()) {
            var module = Path.of(entry.getKey());
            var source = module.resolve("src/test/java/dev/parkcommerce/" + entry.getKey().replace("-service", ""));
            var selected = new java.util.TreeSet<String>();
            try (var files = Files.walk(source)) {
                files.filter(path -> path.getFileName().toString().endsWith("IT.java"))
                    .forEach(path -> selected.add(path.getFileName().toString().replaceFirst("\\.java$", "")));
            }
            for (var name : entry.getValue()) {
                if (!selected.contains(name)) throw new IllegalStateException("Accepted database suite source missing: " + entry.getKey() + "/" + name);
            }
            for (var name : selected) {
                var report = module.resolve("target/failsafe-reports/TEST-dev.parkcommerce." + entry.getKey().replace("-service", "") + "." + name + ".xml");
                if (!Files.isRegularFile(report)) throw new IllegalStateException("Missing integration report: " + report);
                Element suite = parser.newDocumentBuilder().parse(report.toFile()).getDocumentElement();
                if (!"testsuite".equals(suite.getTagName())) throw new IllegalStateException("Invalid integration report: " + report);
                int count = Integer.parseInt(suite.getAttribute("tests"));
                int skipped = Integer.parseInt(suite.getAttribute("skipped"));
                int failures = Integer.parseInt(suite.getAttribute("failures"));
                int errors = Integer.parseInt(suite.getAttribute("errors"));
                if (count == 0 || skipped != 0 || failures != 0 || errors != 0)
                    throw new IllegalStateException("Integration suite incomplete: " + report + " tests=" + count + " skipped=" + skipped + " failures=" + failures + " errors=" + errors);
                System.out.printf("%s/%s: selected=%d executed=%d skipped=%d failures=%d errors=%d%n",
                    entry.getKey(), name, count, count - skipped, skipped, failures, errors);
                suites++;
                tests += count;
            }
        }
        if (suites == 0 || tests == 0) throw new IllegalStateException("No database integration suites executed");
        System.out.printf("PASS database integration selection: %d suites, %d executed tests, 0 skipped%n", suites, tests);
    }
}
