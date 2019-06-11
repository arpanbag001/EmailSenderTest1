package com.innovationredefined.emailsendertest1;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button button_ChooseAccount;
    TextView textView_SelectedAccount;
    Context context;
    private final int ACCOUNT_PICKER_REQUEST_CODE = 1212;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        button_ChooseAccount = findViewById(R.id.button_choose_account);
        textView_SelectedAccount = findViewById(R.id.textView_selected_account);

        button_ChooseAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AccountManager.newChooseAccountIntent(null, null, new String[]{"com.google"},
                        false, null, null, null, null);
                startActivityForResult(intent, ACCOUNT_PICKER_REQUEST_CODE);
            }
        });
    }

    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        if (requestCode == ACCOUNT_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Toast.makeText(context, accountName, Toast.LENGTH_SHORT).show();
            textView_SelectedAccount.setText(accountName);

        }
    }
}
