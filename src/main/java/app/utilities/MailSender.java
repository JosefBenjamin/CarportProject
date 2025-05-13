package app.utilities;


import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import java.io.IOException;

public class MailSender {


    /**
     *
     * @param cpLength length of the carport given by the user in centimeters (cm)
     * @param cpWidth width of the carport given by the user in centimeters (cm)
     * @param cpHeight height of the carport given by the user (or fixed) in centimeters (cm)
     * @param cpRoof though blue plastic is the only roof offered, more options may come available
     * @param cpColor the color of the carport given by user in a single word or two
     *
     * @return true if the mail has succesfully been sent or false if the method failed to the mail to recipient
     * @throws IOException
     *
     */

    public boolean sendCarportRequestMail(String cpLength, String cpWidth, String cpHeight, String cpRoof, String cpColor, String mailRecipient) throws IOException {
        // Erstat xyx@gmail.com med din egen email, som er afsender
        String senderEmail = System.getenv("SENDER_EMAIL");
        Email from = new Email(senderEmail);
        from.setName("Fog Carport");

        Mail mail = new Mail();
        mail.setFrom(from);

        String API_KEY = System.getenv("SENDGRID_API_KEY");

        Personalization personalization = new Personalization();

        /* Erstat kunde@gmail.com, name, email og zip med egne værdier ****/
        /* I test-fasen - brug din egen email, så du kan modtage beskeden */
        personalization.addTo(new Email(mailRecipient));
        personalization.addDynamicTemplateData("cpLength", cpLength + " cm");
        personalization.addDynamicTemplateData("cpWidth", cpWidth + " cm");
        personalization.addDynamicTemplateData("cpHeight", cpHeight + " cm");
        personalization.addDynamicTemplateData("cpRoof", cpRoof);
        personalization.addDynamicTemplateData("cpColor", cpColor);
        mail.addPersonalization(personalization);

        mail.addCategory("carportapp");

        SendGrid sg = new SendGrid(API_KEY);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            // indsæt dit skabelonid herunder
            mail.templateId = "d-39a147a40614413ca498309c70528f16";
            request.setBody(mail.build());
            Response response = sg.api(request);
            return true;
            //For debugging
            // System.out.println(response.getStatusCode());
            // System.out.println(response.getBody());
            // System.out.println(response.getHeaders());

        } catch (IOException e) {
            throw e;
        }
    }

    public boolean sendCarportRequestMail(String cpLength, String cpWidth, String cpHeight, String mailRecipient) throws IOException {
        // Erstat xyx@gmail.com med din egen email, som er afsender
        String senderEmail = System.getenv("SENDER_EMAIL");
        Email from = new Email(senderEmail);
        from.setName("Fog Carport");

        Mail mail = new Mail();
        mail.setFrom(from);

        String API_KEY = System.getenv("SENDGRID_API_KEY");

        Personalization personalization = new Personalization();

        /* Erstat kunde@gmail.com, name, email og zip med egne værdier ****/
        /* I test-fasen - brug din egen email, så du kan modtage beskeden */
        personalization.addTo(new Email(mailRecipient));
        personalization.addDynamicTemplateData("cpLength", cpLength + " cm");
        personalization.addDynamicTemplateData("cpWidth", cpWidth + " cm");
        personalization.addDynamicTemplateData("cpHeight", cpHeight + " cm");
        mail.addPersonalization(personalization);

        mail.addCategory("carportapp");

        SendGrid sg = new SendGrid(API_KEY);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            // indsæt dit skabelonid herunder
            mail.templateId = "d-39a147a40614413ca498309c70528f16";
            request.setBody(mail.build());
            Response response = sg.api(request);
            return true;
            //For debugging
            // System.out.println(response.getStatusCode());
            // System.out.println(response.getBody());
            // System.out.println(response.getHeaders());

        } catch (IOException e) {
            throw e;
        }
    }

}
