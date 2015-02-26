package eBaySellerParser;

import com.ebay.soap.eBLBaseComponents.ItemType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sun.misc.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


//token
//AgAAAA**AQAAAA**aAAAAA**R1TsVA**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6AGkIuoDJWHowidj6x9nY+seQ**lrgCAA**AAMAAA**wYDPchozhcBVlC/p7t3EUlf1emmWhWHpyUtBjLyGfW1J+wapCELriiK0WpkBQ8kVMISqr70JWGajFRZcBxYvMVSXGt0ZRvsOMS6EFQVo75M67PAW7Eq494HejyIxBmEX+/JO6xIwxbw1vh2ETa7pjcSdIOJZYp4/K2wmV5ZcuqfFfsstPcKcsnJpNBbPZIqxH+1LttvqIZSZcGF2zuIXuDfS55ee6laKNeRb/7bvMg941adyip2ROZGQq+98SnYjwzFKHA8ZennwYrgGriMKiTQVexxn26zp3WXNgcuD36LVYCVEZCuXCcQgAZuuWT7Iija2tm+flE7UjDlaOqmPPADNxysgNsOGaQztvxT+ncHGFZK3bIUx7+vhC1DNLNvpF76FRG0Y7WjpToq/cttdgAQYpu/Ngs3XylKLlOwg6IEHu0+4LXLJtqrkGVQ1G1bja2YRkqLq87CHvcOFW391vNHxHUoBx+5YWK674mbAVs2+XH8EQgcn9MAoypboS3TOdAMAGrzF0WOCLtddguhqaADttecKT2nwMWMgXmlFBr5Tmbe2pUn/N8Md8K8nFIpqk/ys1kkkKKigu+s3GtFWnZIOAGrZZrkyB5YBKBVUdHl3L7qNgQ9Vunbr6Dk0A11ItJ4lFWecj4imgCYb6xQtAlpnjIATpcYTPIR/BXfNA4v5sllmwQ0r6ycwLlPpxNruq+YMFcYXkWBpnUFtLBTFHW0rO2qgz9PUBMfGn1Yn2xozQb3TycfRpvOX02dA9QLG


public class Main
{

    static SellerService sellerService;

    public static void main(String[] args)
    {
        // write your code here
        ApplicationContext context = new ClassPathXmlApplicationContext("config.xml");
        sellerService = (SellerService) context.getBean("sellerService");
        List<String> sellerIds;
        try
        {
            String content = new String(Files.readAllBytes(Paths.get("./input.txt")));
            sellerIds = Arrays.asList(content.split(";"));
        }
        catch (IOException e)
        {
            System.out.println("Input.txt file wasn't found. Exception: " + e.toString());
            return;
        }
        System.out.println("");
        System.out.println("-------------------------------------");
        System.out.println("");
        Map<String, List<ItemType>> data = sellerService.getAllItems(sellerIds);
        CsvBuilder csvBuilder = new CsvBuilder();
        String csv = csvBuilder.buildCsv(data);
        try
        {
            File file = new File("./output.csv");
            if (!file.exists())
            {

                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(csv.getBytes(Charset.forName("UTF-8")));
        }
        catch (Exception e)
        {
            System.out.println("Was unable to create output file. Error: " + e.toString());
        }
        if (System.console() != null)
        {
            System.out.println("");
            System.out.println("");
            System.console().readLine("Output file was formed successfully. Please press 'Enter' to exit");
        }

    }
}