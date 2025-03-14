package com.gotcharoom.gdp.mail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.naming.Context;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MailRequest {
    private String mailFrom;
    private String mailTo;
    private String subject;
    private String htmlContents;

    public static class MailRequestBuilder {
        private String mailTo;
        private String subject;
        private String htmlContents;

        public MailRequestBuilder mailTo(String mailTo) {
            this.mailTo = mailTo;
            return this;
        }

        public MailRequestBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public MailRequestBuilder htmlContents(String htmlContents) {
            this.htmlContents = htmlContents;
            return this;
        }

        public MailRequest build() {
            // ✅ mailFrom을 고정
            String mailFrom = "postmaster@stdtrinfra.com";
            return new MailRequest(mailFrom, mailTo, subject, htmlContents);
        }
    }


    public static MailRequestBuilder builder() {
        return new MailRequestBuilder();
    }
}
