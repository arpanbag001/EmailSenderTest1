package com.innovationredefined.emailsendertest1;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity {

    Button button_ChooseAccount, button_Authenticate, button_SendMail;
    TextView textView_SelectedAccount;
    Context context;
    private final int ACCOUNT_PICKER_REQUEST_CODE = 1212;
    private final String AUTH_TOKEN_TYPE = "oauth2:https://mail.google.com/";
    String selectedAccountName;
    Account selectedAccount;
    String oauthTOken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        button_ChooseAccount = findViewById(R.id.button_choose_account);
        button_Authenticate = findViewById(R.id.button_authenticate);
        button_SendMail = findViewById(R.id.button_sendMail);
        textView_SelectedAccount = findViewById(R.id.textView_selected_account);

        button_ChooseAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start account chooser flow
                Intent intent = AccountManager.newChooseAccountIntent(null, null, new String[]{"com.google"},
                        false, null, null, null, null);
                startActivityForResult(intent, ACCOUNT_PICKER_REQUEST_CODE);
            }
        });

        //Start authentication flow
        button_Authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(selectedAccountName)) {
                    AccountManager accountManager = AccountManager.get(context);
                    Account[] accounts = accountManager.getAccountsByType("com.google");

                    for (Account account : accounts
                    ) {
                        if (account.name.equals(selectedAccountName)) {
                            selectedAccount = account;
                            break;
                        }
                    }

                    if (selectedAccount != null) {
                        Bundle options = new Bundle();
                        accountManager.getAuthToken(
                                selectedAccount,               // Account retrieved using getAccountsByType()
                                AUTH_TOKEN_TYPE,                // Auth scope
                                options,                        // Authenticator-specific options
                                MainActivity.this,                           // Your activity
                                new AccountManagerCallback<Bundle>() {
                                    @Override
                                    public void run(AccountManagerFuture<Bundle> result) {
                                        try {
                                            // Get the result of the operation from the AccountManagerFuture.
                                            Bundle bundle = result.getResult();

                                            // The token is a named value in the bundle. The name of the value
                                            // is stored in the constant AccountManager.KEY_AUTHTOKEN.
                                            String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                                            oauthTOken = token;
                                            Toast.makeText(context, token, Toast.LENGTH_SHORT).show();

                                        } catch (OperationCanceledException e) {
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        } catch (IOException e) {
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        } catch (AuthenticatorException e) {
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                ,          // Callback called when a token is successfully acquired
                                null);    // Callback called if an error occurs

                    }

                }
            }
        });

        button_SendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(selectedAccountName) && !TextUtils.isEmpty(oauthTOken))
                    sendTestEmail();
                    //new SendEmailWithJavaMail().execute("");
            }
        });
    }


    //Account chosen
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        if (requestCode == ACCOUNT_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            selectedAccountName = accountName;
            textView_SelectedAccount.setText(accountName);
        }
    }

    private void sendTestEmail() {
        BackgroundMail.newBuilder(this)
                .withUsername(selectedAccountName)
                .withPassword(oauthTOken)
                .withMailto("arpanbag1996@gmail.com")
                //.withMailCc("cc-email@gmail.com")
                //.withMailBcc("bcc-email@gmail.com")
                .withSubject("This is the subject")
                .withBody("This is the body")
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        //do some magic
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        //do some magic
                    }
                })
                .send();
    }

    private class SendEmailWithJavaMail extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String submitEmail = "arpanbag1996@gmail.com";

            Properties props = new Properties();
            props.setProperty("mail.transport.protocol", "smtp");
            props.setProperty("mail.host", "smtp.gmail.com");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.quitwait", "false");
            props.put("mail.smtp.auth.mechanisms", "XOAUTH2");
            Session session = Session.getInstance(props);

            session.setDebug(true);

            Message message = new MimeMessage(session);
            try {
                message.setSubject("This is the subject");

                message.setText("This is the message");

                Address toAddress = new InternetAddress(submitEmail);
                message.setRecipient(Message.RecipientType.TO, toAddress);

                Transport transport = session.getTransport("smtp");
                transport.connect("smtp.gmail.com", selectedAccountName,oauthTOken);
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
