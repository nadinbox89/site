import com.github.nadinbox89.ProductType;
import com.github.nadinbox89.SiteType;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

public class Site {

    public static void main(String[] args) throws Exception {

        File targetDirectory = getTargetDirectory();

        SiteType siteModel = readSiteModel();

        Configuration cfg = getTemplateConfiguration();

        generateIndex(targetDirectory, siteModel, cfg);

        siteModel.getProduct().stream().forEach(product -> generateProduct(targetDirectory, cfg, product));
    }

    private static SiteType readSiteModel() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(SiteType.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        InputSource source = loadSiteDescriptor();
        return ((JAXBElement<SiteType>) unmarshaller.unmarshal(source)).getValue();
    }

    private static void generateIndex(File targetDirectory, SiteType siteModel, Configuration cfg) {
        try(Writer out = new FileWriter(new File(targetDirectory, "index.html"))) {
            Template template = cfg.getTemplate("index-template.html");
            template.process(siteModel, out);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static void generateProduct(File targetDirectory, Configuration cfg, ProductType product) {
        try(Writer out = new FileWriter(new File(targetDirectory, product.getId() + ".html"))) {
            Template prodTemplate = cfg.getTemplate("product-template.html");
            prodTemplate.process(product, out);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static File getTargetDirectory() {
        String targetDir= System.getProperty("target.dir");
        if(targetDir==null || targetDir.isEmpty()){
            throw new IllegalArgumentException("Provide 'target.dir' system property");
        }
        File targetDirectory = new File(targetDir);
        targetDirectory.mkdir();
        return targetDirectory;
    }

    private static Configuration getTemplateConfiguration() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
        cfg.setLocalizedLookup(false);
        cfg.setTemplateLoader(new ClassTemplateLoader(Site.class, ""));
        cfg.setDefaultEncoding("UTF-8");
        return cfg;
    }

    private static InputSource loadSiteDescriptor() {
        return new InputSource(Site.class.getResourceAsStream("site.xml"));
    }
}
