package eBaySellerParser;


import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.call.GetSellerListCall;


//token
//AgAAAA**AQAAAA**aAAAAA**R1TsVA**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6AGkIuoDJWHowidj6x9nY+seQ**lrgCAA**AAMAAA**wYDPchozhcBVlC/p7t3EUlf1emmWhWHpyUtBjLyGfW1J+wapCELriiK0WpkBQ8kVMISqr70JWGajFRZcBxYvMVSXGt0ZRvsOMS6EFQVo75M67PAW7Eq494HejyIxBmEX+/JO6xIwxbw1vh2ETa7pjcSdIOJZYp4/K2wmV5ZcuqfFfsstPcKcsnJpNBbPZIqxH+1LttvqIZSZcGF2zuIXuDfS55ee6laKNeRb/7bvMg941adyip2ROZGQq+98SnYjwzFKHA8ZennwYrgGriMKiTQVexxn26zp3WXNgcuD36LVYCVEZCuXCcQgAZuuWT7Iija2tm+flE7UjDlaOqmPPADNxysgNsOGaQztvxT+ncHGFZK3bIUx7+vhC1DNLNvpF76FRG0Y7WjpToq/cttdgAQYpu/Ngs3XylKLlOwg6IEHu0+4LXLJtqrkGVQ1G1bja2YRkqLq87CHvcOFW391vNHxHUoBx+5YWK674mbAVs2+XH8EQgcn9MAoypboS3TOdAMAGrzF0WOCLtddguhqaADttecKT2nwMWMgXmlFBr5Tmbe2pUn/N8Md8K8nFIpqk/ys1kkkKKigu+s3GtFWnZIOAGrZZrkyB5YBKBVUdHl3L7qNgQ9Vunbr6Dk0A11ItJ4lFWecj4imgCYb6xQtAlpnjIATpcYTPIR/BXfNA4v5sllmwQ0r6ycwLlPpxNruq+YMFcYXkWBpnUFtLBTFHW0rO2qgz9PUBMfGn1Yn2xozQb3TycfRpvOX02dA9QLG


public class Main {

    public static void main(String[] args) {
        // write your code here
        ApiContext apiContext = new ApiContext();
        ApiCredential cred = apiContext.getApiCredential();

        String token = "AgAAAA**AQAAAA**aAAAAA**R1TsVA**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6AGkIuoDJWHowidj6x9nY+seQ**lrgCAA**AAMAAA**wYDPchozhcBVlC/p7t3EUlf1emmWhWHpyUtBjLyGfW1J+wapCELriiK0WpkBQ8kVMISqr70JWGajFRZcBxYvMVSXGt0ZRvsOMS6EFQVo75M67PAW7Eq494HejyIxBmEX+/JO6xIwxbw1vh2ETa7pjcSdIOJZYp4/K2wmV5ZcuqfFfsstPcKcsnJpNBbPZIqxH+1LttvqIZSZcGF2zuIXuDfS55ee6laKNeRb/7bvMg941adyip2ROZGQq+98SnYjwzFKHA8ZennwYrgGriMKiTQVexxn26zp3WXNgcuD36LVYCVEZCuXCcQgAZuuWT7Iija2tm+flE7UjDlaOqmPPADNxysgNsOGaQztvxT+ncHGFZK3bIUx7+vhC1DNLNvpF76FRG0Y7WjpToq/cttdgAQYpu/Ngs3XylKLlOwg6IEHu0+4LXLJtqrkGVQ1G1bja2YRkqLq87CHvcOFW391vNHxHUoBx+5YWK674mbAVs2+XH8EQgcn9MAoypboS3TOdAMAGrzF0WOCLtddguhqaADttecKT2nwMWMgXmlFBr5Tmbe2pUn/N8Md8K8nFIpqk/ys1kkkKKigu+s3GtFWnZIOAGrZZrkyB5YBKBVUdHl3L7qNgQ9Vunbr6Dk0A11ItJ4lFWecj4imgCYb6xQtAlpnjIATpcYTPIR/BXfNA4v5sllmwQ0r6ycwLlPpxNruq+YMFcYXkWBpnUFtLBTFHW0rO2qgz9PUBMfGn1Yn2xozQb3TycfRpvOX02dA9QLG";

        cred.seteBayToken("");

    }
}