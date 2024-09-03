package service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

public class SmsService {
    // Find your Account Sid and Token at twilio.com/console
    public static final String ACCOUNT_SID = "AC9d500356237dbcf2e55035d10283a120";
    public static final String AUTH_TOKEN = "5b63c3d573b6c2eb2a4a48286d604c1a";
//HPU6KXEYLR3JZLMJHRXQDBL2

    public void send(String code){
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                        new com.twilio.type.PhoneNumber("+21629935253"),
                        new com.twilio.type.PhoneNumber("+13854429497" +
                                "\n"),
                        "pls use this code to connect :"+code)
                .create();

        System.out.println(message.getSid());
    }
}
