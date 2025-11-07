package com.romanm.smsserver.model;

import java.util.Set;

public interface SmsThreadNotifier {
    public void realSendAddressats(Set<String> sendTels);
}
