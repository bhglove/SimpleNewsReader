package edu.cpsc4820.bhglove.clemsonnews;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SelectCategory extends AppCompatActivity {
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private TextView mEmptyText;
    private DataModel data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        data = DataModel.getInstance();

        mListView = (ListView) findViewById(R.id.categoryListView);
        mEmptyText = (TextView) findViewById(R.id.textView);

        mEmptyText.setVisibility(View.INVISIBLE);


        //Copies all of the feeds into that list of available feeds



        //Set the adapter for the selected feed list (on main screen)
        mAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, data.getmListSelected()){

            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

            /*YOUR CHOICE OF COLOR*/
                textView.setTextColor(Color.BLUE);

                return view;
            }
        };

        if(data.getmListSelected().isEmpty()){
            mEmptyText.setText("List is empty");
            mEmptyText.setVisibility(View.VISIBLE);
        }
        else{
            mEmptyText.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.VISIBLE);
        }


        mListView.setAdapter(mAdapter);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder optionsBuilder = new AlertDialog.Builder(SelectCategory.this);
                optionsBuilder.setTitle("Options");
                optionsBuilder.setSingleChoiceItems(new String[]{"Delete"}, 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.dismiss();
                        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(SelectCategory.this);
                        deleteBuilder.setTitle("Confirm Delete");

                        String item = data.getmListSelected().get(position).toString();
                        deleteBuilder.setMessage("Are you sure you want to delete " + item.toLowerCase() + " feed?");

                        deleteBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("Index", "Index at: " + position);
                                data.addToAvailableFeed(data.getmListSelected().get(position).toString());
                                mAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });

                        deleteBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        //Closes the dialog for Builder
                        dialog.dismiss();
                        deleteBuilder.show();
                    }

                });
                optionsBuilder.show();
                return true;
            }
        });

        Button addButton = (Button) findViewById(R.id.buttonAddCat);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SelectCategory.this);
                dialogBuilder.setTitle("Select Category");
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, data.getmListAvailable()){

                    @Override
                    public View getView(int position, View convertView,
                                        ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);

                        TextView textView=(TextView) view.findViewById(android.R.id.text1);

            /*YOUR CHOICE OF COLOR*/
                        textView.setTextColor(Color.BLUE);

                        return view;
                    }
                };

                dialogBuilder.setSingleChoiceItems(adapter, 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        data.addToSelectedFeed(data.getmListAvailable().get(which).toString());
                        adapter.notifyDataSetChanged();
                        mAdapter.notifyDataSetChanged();
                        mListView.setVisibility(View.VISIBLE);
                        mEmptyText.setVisibility(View.INVISIBLE);
                        dialog.dismiss();
                    }
                });


                dialogBuilder.create();
                dialogBuilder.show();
            }
        });
    }
}