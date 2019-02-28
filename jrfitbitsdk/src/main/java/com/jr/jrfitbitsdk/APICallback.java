package com.jr.jrfitbitsdk;

import com.jr.jrfitbitsdk.model.CodeBean;

public interface APICallback {
    void onGetPkceCode(CodeBean bean);

    void onRefreshToken(CodeBean bean);
}
