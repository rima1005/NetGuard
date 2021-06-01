package eu.faircode.netguard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActivityCipher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cipher);
        Bundle b = getIntent().getExtras();
        String address = b.getString("address");
        int port = b.getInt("port");
        String dnsAddress = "";

        int hostId = -1;
        String defaultCipher = "";
        String defaultProtocol = "";

        Cursor dnslookup = DatabaseHelper.getInstance(getApplicationContext()).getAccessDnsAllowed(address);
        if(dnslookup.moveToFirst())
            dnsAddress = dnslookup.getString(dnslookup.getColumnIndex("resource"));

        String finalName = dnsAddress == null || dnsAddress.isEmpty() ? address : dnsAddress;
        Objects.requireNonNull(getSupportActionBar()).setTitle(address);
        if(!address.equals(finalName))
            Objects.requireNonNull(getSupportActionBar()).setSubtitle(finalName);

        Cursor host = DatabaseHelper.getInstance(getApplicationContext()).getHost(finalName, port);
        if(host.moveToFirst())
        {
            hostId = host.getInt(host.getColumnIndex("ID"));
            defaultCipher = host.getString(host.getColumnIndex("suit"));
            defaultProtocol = host.getString(host.getColumnIndex("protocol"));
        }

        TextView handshakeDetails = findViewById(R.id.tvHandshakeDetails);
        if(defaultCipher == null || defaultCipher == "" && defaultProtocol == null || defaultProtocol == "")
        {
            handshakeDetails.setText("The server seems to have certificate pinning or refused the TLS connection for other reasions!");
        }
        else
        {
            handshakeDetails.setText(defaultProtocol + " with " + defaultCipher);
        }


        Cursor suits = null;
        Cursor protocols = null;

        if(hostId != -1)
        {
            suits = DatabaseHelper.getInstance(getApplicationContext()).getSupportedSuits(hostId);
            protocols = DatabaseHelper.getInstance(getApplicationContext()).getSupportedProtocols(hostId);
        }
        List<String> supportedSuits = new ArrayList<>();
        List<String> supportedProtocols = new ArrayList<>();

        if(suits != null && suits.moveToFirst())
        {
            do {
                supportedSuits.add(suits.getString(suits.getColumnIndex("name")));
            } while(suits.moveToNext());

            suits.close();
        }

        if(protocols != null && protocols.moveToFirst())
        {
            do {
                supportedProtocols.add(protocols.getString(protocols.getColumnIndex("name")));
            } while(protocols.moveToNext());

            protocols.close();
        }

        ListView lvSuits = findViewById(R.id.lvDetailsCipherSuits);
        ListView lvProtocols = findViewById(R.id.lvDetailsProtocols);

        ArrayAdapter<String> suitsAdapter = new ArrayAdapter<>(this, R.layout.simple_listview, supportedSuits);
        ArrayAdapter<String> protocolsAdapter = new ArrayAdapter<>(this, R.layout.simple_listview, supportedProtocols);

        lvSuits.setAdapter(suitsAdapter);
        lvProtocols.setAdapter(protocolsAdapter);

        lvSuits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent webInfo = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ciphersuite.info/cs/" + parent.getItemAtPosition(position).toString()));
                startActivity(webInfo);
            }
        });
    }
}