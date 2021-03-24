package de.armbrust.planz.amazonapi;

import com.amazon.sellingpartner.model.ReportDocument;
import com.amazon.spapi.documents.DownloadBundle;
import com.amazon.spapi.documents.DownloadHelper;
import com.amazon.spapi.documents.DownloadSpecification;
import com.amazon.spapi.documents.exception.CryptoException;
import com.amazon.spapi.documents.exception.HttpResponseException;
import com.amazon.spapi.documents.exception.MissingCharsetException;
import com.amazon.spapi.documents.impl.AESCryptoStreamFactory;
import com.amazon.spapi.documents.impl.OkHttpTransferClient;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DownloadAndDecryptService {

    public DownloadHelper getDownloadHelper() {
        OkHttpTransferClient okHttpTransferClient = new OkHttpTransferClient.Builder().build();

        DownloadHelper downloadHelper = new DownloadHelper.Builder()
                .withHttpTransferClient(okHttpTransferClient)
                .withTmpFileDirectory(null)
                .build();

        return downloadHelper;
    }


    public AESCryptoStreamFactory getAESCryptoStreamFactory(String secretKey, String initializationVector) {

        AESCryptoStreamFactory aesCryptoStreamFactory = new AESCryptoStreamFactory
                .Builder(secretKey,
                initializationVector)
                .build();

        return aesCryptoStreamFactory;
    }

    public DownloadSpecification getDownloadSpecification(AESCryptoStreamFactory aesCryptoStreamFactory, String reportDownloadURL) {

        DownloadSpecification downloadSpecification = new DownloadSpecification.Builder(aesCryptoStreamFactory, reportDownloadURL)
                .withCompressionAlgorithm(null)
                .build();

        return downloadSpecification;
    }

    public DownloadBundle getDecryptedDownloadBundle(ReportDocument reportDownloadInformations) throws IOException, HttpResponseException, CryptoException, MissingCharsetException {

        DownloadHelper downloadHelper = getDownloadHelper();
        String secretKey = reportDownloadInformations.getEncryptionDetails().getKey();
        String initializationVector = reportDownloadInformations.getEncryptionDetails().getInitializationVector();
        String downloadUrl = reportDownloadInformations.getUrl();

        AESCryptoStreamFactory aesCryptoStreamFactory = getAESCryptoStreamFactory(secretKey, initializationVector);
        DownloadSpecification downloadSpecification = getDownloadSpecification(aesCryptoStreamFactory, downloadUrl);

        DownloadBundle downloadedFileBundle = downloadHelper.download(downloadSpecification);

        return downloadedFileBundle;
    }

}
