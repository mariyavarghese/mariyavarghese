package com.oges.ttsec.util;

import com.oges.ttsec.model.ContactModel;

public interface ScanStatusCallback {
    void onSuccess(ContactModel contactModel);
    void onError();
}
