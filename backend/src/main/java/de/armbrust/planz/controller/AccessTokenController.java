package de.armbrust.planz.controller;


import de.armbrust.planz.amazonapi.SellersApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("documentID")
public class AccessTokenController {

    private final SellersApiService sellersApiService;

    @Autowired
    public AccessTokenController(SellersApiService sellersApiService) {
        this.sellersApiService = sellersApiService;
    }

    @GetMapping
    public String getReportDocumentId() {
        return sellersApiService.createReportAndGetReportDocumentId();
    }
}
