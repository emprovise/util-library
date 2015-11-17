package com.emprovise.util.commons;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Email Utility to send simple text email.
 * @see <a href="http://www.tutorialspoint.com/java/java_sending_email.htm">code reference</a>
 */
public class EmailUtil {
	
	/**
	 * Java mail session instance with the smtp server to send email.
	 */
	private Session session;
	
	/**
	 * A Regular Expression Pattern to verify an email address.
	 */
	public static final Pattern EMAIL_PATTERN = Pattern.compile( "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

	/**
	 * Creates a new EmailUtil instance by setting the smtp-host and initializing mail session.
	 *
	 * @param host
	 * 		String host name of the email server to be used to send the emails.
	 */
	public EmailUtil(String host) {
		super();

		  // Get system properties
		  Properties properties = System.getProperties();

		  // Setup mail server
		  properties.setProperty("mail.smtp.host", host);

		  // Get the Session object.
		  session = Session.getInstance(properties);
	}

	/**
	 * Sends an simple text/html email message if no file attachments are passed.
	 * When the list of attachment files are passed then they are send as attachment with the email.
	 *
	 * @param senderAddress
	 * 		Sender's email ID
	 * @param recipientAddressList
	 * 		List of Recipient's email IDs.
	 * @param subject
	 * 		Subject of the email
	 * @param textContent
	 * 		Text/HTML content of the email
	 * @param attachments
	 * 		List of {@link DataSource} to be attached with the email, could be {@link FileDataSource}, {@link ByteArrayDataSource} etc.
	 * @return true when the email is sent successfully else false
	 */
	private boolean sendEmail(String senderAddress, List<String> recipientAddressList, String subject, String textContent, List<DataSource> attachments) {

		try {
			validateEmailAddress(senderAddress);

			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(senderAddress));

			// Set To: header field of the header.
			if(recipientAddressList != null && recipientAddressList.size() == 1) {
                validateEmailAddress(recipientAddressList.get(0));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientAddressList.get(0).trim()));
			}
			else if(recipientAddressList != null) {

                String[] emailAddresses = recipientAddressList.toArray(new String[recipientAddressList.size()]);
                validateEmailAddress(emailAddresses);

                Address[] addressList = new Address[recipientAddressList.size()];
				int counter = 0;
				for (String address : recipientAddressList) {
					addressList[counter] = new InternetAddress(address.trim());
					counter++;
				}

				message.addRecipients(Message.RecipientType.TO, addressList);
			}

			// Set Subject: header field
			message.setSubject(subject);

			if(attachments != null && !attachments.isEmpty()) {

				// Create a multipar message
				Multipart multipart = new MimeMultipart("mixed");

				// Create the message part
				BodyPart messageBodyPart = new MimeBodyPart();
				// Now set the actual message
				messageBodyPart.setContent(textContent, "text/html");

				for (DataSource source : attachments) {
					// Create the message part
					BodyPart mimeBodyPart = new MimeBodyPart();
					mimeBodyPart.setDataHandler(new DataHandler(source));
					mimeBodyPart.setFileName(source.getName());
					// Add email attachments
					multipart.addBodyPart(mimeBodyPart);
				}
				message.setContent(multipart);
			}
			else {
				// Send the actual HTML message, as big as you like
				message.setContent(textContent, "text/html");
			}

			// Send message
			Transport.send(message);
			return true;

		} catch (MessagingException mex) {
			mex.printStackTrace();
			return false;
		}
	}

    /**
     * Sends an simple text/html email message if no file attachments are passed.
     * When the list of attachment files are passed then they are send as attachment with the email.
     *
     * @param senderAddress
     * 		Sender's email ID
     * @param recipientAddressList
     * 		List of Recipient's email IDs.
     * @param subject
     * 		Subject of the email
     * @param textContent
     * 		Text/HTML content of the email
     * @param attachments
     * 		List of {@link File} to be attached with the email.
     * @return true when the email is sent successfully else false
     */
    public boolean sendEmail(String senderAddress, List<String> recipientAddressList, String subject, String textContent, File... attachments) {

        List<DataSource> dataSources = new ArrayList<>();

        for (File file : attachments) {
            dataSources.add(new FileDataSource(file));
        }

        return sendEmail(senderAddress, recipientAddressList, subject, textContent, dataSources);
	}

    /**
     * Sends an simple text/html email message if no attachments are passed.
     * When the map of attachments with filename as the key and the byte[] as data is passed then they are send as attachment with the email.
     *
     * @param senderAddress
     * 		Sender's email ID
     * @param recipientAddressList
     * 		List of Recipient's email IDs.
     * @param subject
     * 		Subject of the email
     * @param textContent
     * 		Text/HTML content of the email
     * @param attachments
     * 		Map of {@link String} which is the file name and the bye[] as the data to be attached with the email.
     * @return true when the email is sent successfully else false
     */
    public boolean sendEmail(String senderAddress, List<String> recipientAddressList, String subject, String textContent, Map<String, byte[]> attachments) {

        List<DataSource> dataSources = new ArrayList<>();
        FileTypeMap defaultFileTypeMap = MimetypesFileTypeMap.getDefaultFileTypeMap();

        for (Map.Entry<String, byte[]> entry : attachments.entrySet()) {
            ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(entry.getValue(), defaultFileTypeMap.getContentType(entry.getKey()));
            byteArrayDataSource.setName(entry.getKey());
            dataSources.add(byteArrayDataSource);
        }

        return sendEmail(senderAddress, recipientAddressList, subject, textContent, dataSources);
    }

	/**
	 * Sends an simple text/html email message if no file attachments are passed.
	 * When the list of attachment files are passed then they are send as attachment with the email.
	 *
	 * @param senderAddress
	 * 		Sender's email ID
	 * @param recipients
	 * 		Comma seperated list of Recipient's email IDs.
	 * @param subject
	 * 		Subject of the email
	 * @param textContent
	 * 		Text/HTML content of the email
	 * @param attachments
	 * 		List of files to be attached with the email
	 * @return true when the email is sent successfully else false
	 */
	public boolean sendEmail(String senderAddress, String recipients, String subject, String textContent, File... attachments) {
		List<String> addresses = Arrays.asList(recipients.split("\\s*,\\s*"));
		return sendEmail(senderAddress, addresses, subject, textContent, attachments);
	}

	public boolean sendEmail(String senderAddress, String recipients, String subject, String textContent, Map<String, byte[]> attachments) {
		List<String> addresses = Arrays.asList(recipients.split("\\s*,\\s*"));
		return sendEmail(senderAddress, addresses, subject, textContent, attachments);
	}

    public static void validateEmailAddress(String... emailAddresses) {

        if (emailAddresses != null && emailAddresses.length > 0) {
            for (String emailAddress : emailAddresses) {

                Matcher matcher = EMAIL_PATTERN.matcher(emailAddress);

                if (!matcher.matches()) {
                    throw new IllegalArgumentException("Invalid Email Address : " + emailAddress);
                }
            }
        }
    }
}
