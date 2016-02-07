package edu.cpsc4820.bhglove.simplenewsreader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectCategory extends AppCompatActivity {
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private TextView mEmptyText;
    private DataModel data;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SelectCategory.this, NewsFeed.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        data = DataModel.getInstance();

        mListView = (ListView) findViewById(R.id.categoryListView);
        mEmptyText = (TextView) findViewById(R.id.textView);

        mEmptyText.setVisibility(View.INVISIBLE);

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
            mEmptyText.setText(R.string.selected);
            mEmptyText.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);
        }

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder optionsBuilder = new AlertDialog.Builder(SelectCategory.this);
                //String title = PopularFeeds.valueOf(data.getmListSelected().get(position).toString()).toReadableString();
                String title = data.getmListSelected().get(position).toString();
                optionsBuilder.setTitle("Options for " + title);

                optionsBuilder.setItems(new String[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String item = data.getmListSelected().get(position).toString();
                        if (which == 0) {
                            AlertDialog.Builder editBuilder = new AlertDialog.Builder(SelectCategory.this);
                            TextView rssTitleLabel = new TextView(SelectCategory.this);
                            TextView rssLinkLabel = new TextView(SelectCategory.this);
                            final EditText rssTitle = new EditText(SelectCategory.this);
                            final EditText rssLink = new EditText(SelectCategory.this);

                            editBuilder.setTitle("Edit");

                            //String title = PopularFeeds.valueOf(item).toReadableString();
                            //String feed = PopularFeeds.valueOf(item).toFeed();
                            String title = item;
                            String feed = data.findLink(title);
                            rssTitle.setText(title);
                            rssLink.setText(feed);
                            rssTitle.setTextColor(Color.BLACK);
                            rssLink.setTextColor(Color.BLACK);

                            rssTitleLabel.setText("RSS Title");
                            rssLinkLabel.setText("RSS Link");


                            LinearLayout layout = new LinearLayout(getApplicationContext());
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.setGravity(Gravity.CENTER_VERTICAL);
                            layout.addView(rssTitleLabel);
                            layout.addView(rssTitle);
                            layout.addView(rssLinkLabel);
                            layout.addView(rssLink);
                            editBuilder.setView(layout);
                            editBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String title = rssTitle.getText().toString();
                                    String link = rssLink.getText().toString();
                                    if (!title.isEmpty() && !link.isEmpty()) {
                                        data.editFeed(item, title, link);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                            editBuilder.setNegativeButton("Cancel", null);
                            editBuilder.show();
                        }
                        if (which == 1) {
                            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(SelectCategory.this);
                            deleteBuilder.setTitle("Confirm Delete");
                            deleteBuilder.setMessage("Are you sure you want to delete " + item + " feed?");
                            deleteBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("Index", "Index at: " + position);
                                    data.addToAvailableFeed(data.getmListSelected().get(position).toString());
                                    mAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                    if (data.getmListSelected().size() == 0) {
                                        mEmptyText.setText("List is empty");
                                        mEmptyText.setVisibility(View.VISIBLE);
                                    }
                                }
                            });

                            deleteBuilder.setNegativeButton("Cancel", null);
                            //Closes the dialog for Builder
                            dialog.dismiss();
                            deleteBuilder.show();
                        }
                    }
                });
                optionsBuilder.show();
            }
        });

        Button addButton = (Button) findViewById(R.id.buttonAddCat);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SelectCategory.this);
                dialogBuilder.setTitle("Select Category");
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, data.getmListAvailable()) {

                    @Override
                    public View getView(int position, View convertView,
                                        ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);

                        TextView textView = (TextView) view.findViewById(android.R.id.text1);

                        /*YOUR CHOICE OF COLOR*/
                        textView.setTextColor(Color.BLUE);

                        return view;
                    }
                };

                dialogBuilder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        data.addToSelectedFeed(data.getmListAvailable().get(which).toString());
                        adapter.notifyDataSetChanged();
                        mAdapter.notifyDataSetChanged();
                        mListView.setVisibility(View.VISIBLE);
                        mEmptyText.setText(R.string.selected);
                        mEmptyText.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });
                dialogBuilder.setNeutralButton("Add Custom RSS Feed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SelectCategory.this);
                                dialogBuilder.setTitle("Add Custom RSS");

                                final EditText rssTitle = new EditText(getApplicationContext());

                                rssTitle.setHint("Feed Name");
                                rssTitle.setTextColor(Color.BLACK);
                                rssTitle.setHintTextColor(Color.GRAY);


                                final EditText rssLink = new EditText(getApplicationContext());
                                rssLink.setHint("Feed Link");
                                rssLink.setTextColor(Color.BLACK);
                                rssLink.setHintTextColor(Color.GRAY);


                                LinearLayout layout = new LinearLayout(getApplicationContext());
                                layout.setOrientation(LinearLayout.VERTICAL);
                                layout.setGravity(Gravity.CENTER_VERTICAL);
                                layout.addView(rssTitle);
                                layout.addView(rssLink);

                                dialogBuilder.setView(layout);
                                dialogBuilder.setNegativeButton("Cancel", null);
                                dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String title = rssTitle.getText().toString();
                                                String link = rssLink.getText().toString();
                                                if (!title.isEmpty() && !link.isEmpty()) {
                                                    data.createNewFeed(title, link);
                                                    if (data.getmListSelected().size() == 0) {
                                                        mEmptyText.setText(R.string.selected);
                                                        mEmptyText.setVisibility(View.VISIBLE);
                                                    }
                                                    data.getmListSelected().add(title);
                                                    mAdapter.notifyDataSetChanged();
                                                    Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                );
                                dialogBuilder.show();
                            }
                        }

                );

                dialogBuilder.create();
                dialogBuilder.show();
            }
        });
    }
}