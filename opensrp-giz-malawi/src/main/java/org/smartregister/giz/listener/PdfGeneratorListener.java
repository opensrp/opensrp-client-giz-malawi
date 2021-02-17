package org.smartregister.giz.listener;


import org.smartregister.giz.util.PdfGeneratorHelper;

interface PdfGeneratorContract {
    void onSuccess(PdfGeneratorHelper.SuccessResponse response);

    void showLog(String log);

    void onStartPDFGeneration();

    void onFinishPDFGeneration();

    void onFailure(PdfGeneratorHelper.FailureResponse failureResponse);
}

public abstract class PdfGeneratorListener implements PdfGeneratorContract {
    @Override
    public void showLog(String log) {

    }

    @Override
    public void onSuccess(PdfGeneratorHelper.SuccessResponse response) {

    }

    @Override
    public void onFailure(PdfGeneratorHelper.FailureResponse failureResponse) {

    }
}
