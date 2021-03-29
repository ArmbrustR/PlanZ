package de.armbrust.planz.amazonapi;

import com.amazon.sellingpartner.model.ReportDocument;
import com.amazon.spapi.documents.DownloadBundle;
import com.amazon.spapi.documents.DownloadHelper;
import com.amazon.spapi.documents.DownloadSpecification;
import com.amazon.spapi.documents.exception.HttpResponseException;
import com.amazon.spapi.documents.impl.AESCryptoStreamFactory;
import com.amazon.spapi.documents.impl.OkHttpTransferClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
@Slf4j
public class DownloadAndDecryptService {

    public DownloadHelper getDownloadHelper() {
        OkHttpTransferClient okHttpTransferClient = new OkHttpTransferClient.Builder().build();

        return new DownloadHelper.Builder()
                .withHttpTransferClient(okHttpTransferClient)
                .withTmpFileDirectory(null)
                .build();
    }


    public AESCryptoStreamFactory getAESCryptoStreamFactory(String secretKey, String initializationVector) {

        return new AESCryptoStreamFactory
                .Builder(secretKey,
                initializationVector)
                .build();
    }

    public DownloadSpecification getDownloadSpecification(AESCryptoStreamFactory aesCryptoStreamFactory, String reportDownloadURL) {

        return new DownloadSpecification.Builder(aesCryptoStreamFactory, reportDownloadURL)
                .withCompressionAlgorithm(null)
                .build();
    }

    public DownloadBundle getDecryptedDownloadBundle(ReportDocument reportDownloadInformations) {
        try {
            DownloadHelper downloadHelper = getDownloadHelper();
            String secretKey = reportDownloadInformations.getEncryptionDetails().getKey();
            String initializationVector = reportDownloadInformations.getEncryptionDetails().getInitializationVector();
            String downloadUrl = reportDownloadInformations.getUrl();

            AESCryptoStreamFactory aesCryptoStreamFactory = getAESCryptoStreamFactory(secretKey, initializationVector);
            DownloadSpecification downloadSpecification = getDownloadSpecification(aesCryptoStreamFactory, downloadUrl);

            DownloadBundle downloadedFileBundle = downloadHelper.download(downloadSpecification);
            return downloadedFileBundle;

        } catch (HttpResponseException | IOException e) {
            throw new RuntimeException("Error in getDecryptedDownloadBundle", e);
        }
    }
}
