package rh.intuitive.care.teste.teste01;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DowloadPDF {
    private static final String URL = "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos";
    private static final String DOWNLOAD_FOLDER = "downloads";
    private static final String ZIP_FILE = "anexos_ans.zip";
    
    public static void starDowload() throws IOException{
        Files.createDirectories(Paths.get(DOWNLOAD_FOLDER));
        Document doc = Jsoup.connect(URL).get();
        Elements links = doc.select("a[href]");

        for (Element link : links) {
            String href = link.attr("abs:href");
            if (href.contains("Anexo") && href.endsWith(".pdf")) {
                downloadFile(href, DOWNLOAD_FOLDER);
            }
        }
        zipFiles(DOWNLOAD_FOLDER, ZIP_FILE);
        System.out.println("Arquivos compactados em " + ZIP_FILE);
    }

    private static void downloadFile(String fileURL, String saveDir) throws IOException {
        URL url = new URL(fileURL);
        String fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
        Path targetPath = Paths.get(saveDir, fileName);
        try (InputStream in = url.openStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void zipFiles(String sourceDir, String zipFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            Files.walk(Paths.get(sourceDir)).filter(Files::isRegularFile).forEach(path -> {
                try (InputStream fis = Files.newInputStream(path)) {
                    ZipEntry zipEntry = new ZipEntry(path.getFileName().toString());
                    zos.putNextEntry(zipEntry);
                    fis.transferTo(zos);
                    zos.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
