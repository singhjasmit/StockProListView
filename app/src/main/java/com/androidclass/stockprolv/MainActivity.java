package com.androidclass.stockprolv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidclass.stockprolv.service.Stock;
import com.androidclass.stockprolv.service.StockService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final int PORT_MGR = 300;
    boolean showMore = true;
    boolean syncEnabled = true;
    StockService ss;
    EditText edSymbol, edQty;
    Button btnAdd;
    TextView txTotalValue;
    ImageView ivSetting;
    ListView myListView;
    MyCustomAdapter myAdapter;
    private ArrayList<StockHolding> userStocks = new ArrayList<StockHolding>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        edSymbol = (EditText) findViewById(R.id.txt_symbol);
        edQty = (EditText) findViewById(R.id.txt_qty);
        btnAdd = (Button) findViewById(R.id.add_button);
        ivSetting = (ImageView) findViewById(R.id.setting);
        myListView = (ListView) findViewById(R.id.lv);


        // Create the adapter to convert the array to views
        myAdapter = new MyCustomAdapter(this, userStocks);
        myListView.setAdapter(myAdapter);

        // React to user clicks on item
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                                    long id) {


                StockHolding sh = myAdapter.getItem(position);
                showDetail(sh);

            }
        });


        ivSetting.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             showSettings();

                                         }
                                     }
        );


        btnAdd.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          String sym = edSymbol.getText().toString();
                                          String qtyStr = edQty.getText().toString();
                                          int qty = 1;


                                          if ((sym == null) || sym.isEmpty()) {
                                              Toast.makeText(getBaseContext(), "Stock Symbol is required", Toast.LENGTH_SHORT).show();
                                          } else {
                                              Stock st = ss.getStock(sym.toUpperCase());
                                              if (st != null) {

                                                  if ((qtyStr != null) ) {
                                                      try {
                                                          qty = Integer.parseInt(qtyStr);
                                                      }
                                                      catch(Exception e)
                                                      {
                                                          qty = -1;
                                                      }
                                                      if (qty < 1) qty = 1;
                                                  }

                                                  if (!updateUserHolding(sym.toUpperCase(), qty)) {
                                                      StockHolding sh = new StockHolding(sym.toUpperCase(), qty);
                                                      userStocks.add(sh);
                                                  }


                                                  updateRows();

                                              } else {
                                                  Toast.makeText(getBaseContext(), "Stock Symbol is not available", Toast.LENGTH_SHORT).show();

                                              }
                                          }

                                      }
                                  }

        );
        ss = new StockService();
        initializeUserPortfolio();
        updateRows();


    }


    private void initializeUserPortfolio() {
        userStocks.add(new StockHolding("MSFT", 5));

    }


    private boolean updateUserHolding(String symbol, int qty) {

        boolean updated = false;
        for (StockHolding s : userStocks) {
            if (s.getSymbol().equals(symbol)) {
                s.setQty(s.getQty() + qty);
                updated = true;
                break;
            }
        }
        return updated;
    }


    private void updateRows() {

        myAdapter.notifyDataSetChanged();

    }


    private void showDetail(StockHolding sh) {

        Intent i = new Intent(this, StockDetailActivity.class);
        Bundle b = new Bundle();
        b.putParcelable(Constants.STOCK, sh);
        b.putBoolean(Constants.SHOWMORE, showMore);
        i.putExtras(b);
        startActivity(i);


    }

    private void showSettings() {

        Intent i = new Intent(this, SettingActivity.class);
        Bundle b = new Bundle();
        b.putBoolean(Constants.SHOWMORE, showMore);
        b.putBoolean(Constants.SYNC_ON, syncEnabled);

        i.putExtras(b);
        startActivityForResult(i, PORT_MGR);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PORT_MGR) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                showMore = data.getBooleanExtra(Constants.SHOWMORE, true);
                syncEnabled = data.getBooleanExtra(Constants.SYNC_ON, true);

            }
        }
    }

}





