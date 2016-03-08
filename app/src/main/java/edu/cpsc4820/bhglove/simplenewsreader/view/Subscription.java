package edu.cpsc4820.bhglove.simplenewsreader.view;

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

import edu.cpsc4820.bhglove.simplenewsreader.R;
import edu.cpsc4820.bhglove.simplenewsreader.controller.DatabaseController;

/***
 * 2/17/2016 SelectCategory renamed to Subscription
 *
 * Creates two listviews displaying the selected and available  RSS Feeds for the User.
 */
public class Subscription extends AppCompatActivity {
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private TextView mEmptyText;
    private DatabaseController data;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Subscription.this, NewsFeed.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        if(data == null) {
           data = DatabaseController.getInstance(getApplicationContext());
        }

        mListView = (ListView) findViewById(R.id.categoryListView);
        mEmptyText = (TextView) findViewById(R.id.textView);

        mEmptyText.setVisibility(View.INVISIBLE);

        //Set the adapter for the selected feed list (on main screen)

        mAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, data.getSelected()){

            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

            /*YOUR CHOICE OF COLOR*/
                textView.setTextColor(Color.BLACK);

                return view;
            }
        };


        //Dispay a text view alerting the user that the list is empty.
        if(data.getSelected().isEmpty()){
            mListView.setVisibility(View.INVISIBLE);
            mEmptyText.setText(R.string.empty_subscriptions);
            mEmptyText.setVisibility(View.VISIBLE);
        }
        else{
            mEmptyText.setText(R.string.selected);
            mEmptyText.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);
        }

        mListView.setAdapter(mAdapter);

        //Alert for managing the RSS Feed
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder optionsBuilder = new AlertDialog.Builder(Subscription.this);
                //String title = PopularFeeds.valueOf(data.getSelected().get(position).toString()).toReadableString();
                String title = data.getSelected().get(position).toString();
                optionsBuilder.setTitle("Options for " + title);

                optionsBuilder.setItems(new String[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String item = data.getSelected().get(position).toString();

                        //** Creates a dialog for editing the selected RSS Feed. **/
                        if (which == 0) {
                            AlertDialog.Builder editBuilder = new AlertDialog.Builder(Subscription.this);
                            TextView rssTitleLabel = new TextView(Subscription.this);
                            TextView rssLinkLabel = new TextView(Subscription.this);
                            final EditText rssTitle = new EditText(Subscription.this);
                            final EditText rssLink = new EditText(Subscription.this);

                            editBuilder.setTitle("Edit");

                            //String title = PopularFeeds.valueOf(item).toReadableString();
                            //String feed = PopularFeeds.valueOf(item).toFeed();
                            //** Formatting for the dialog text. **/
                            String title = item;
                            String feed = data.findLink(title);


                            rssTitle.setText(title);
                            rssLink.setText(feed);
                            rssTitle.setTextColor(Color.BLACK);
                            rssLink.setTextColor(Color.BLACK);
                            rssTitle.setSelection(rssTitle.getText().toString().length());
                            rssLink.setSelection(rssLink.getText().toString().length());
                            rssTitleLabel.setText("RSS Title");
                            rssLinkLabel.setText("RSS Link");

                            /** Formating for the layout of the dialog **/
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
                                        mAdapter.clear();
                                        mAdapter.addAll(data.getSelected());
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                            editBuilder.setNegativeButton("Cancel", null);
                            editBuilder.show();
                        }
                        /** End of edit dialog. **/

                        /** Creates a dialog for deleting a RSS Feed **/
                        if (which == 1) {
                            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(Subscription.this);
                            deleteBuilder.setTitle("Confirm Delete");
                            deleteBuilder.setMessage("Are you sure you want to delete " + item + " feed?");

                            //Confirm Dialog to confirm deletion.
                            deleteBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                /** Prompt the user for deletion. **/
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("Index", "Index at: " + position);

                                    data.setAvailable(data.getSelected().get(position).toString());

                                    mAdapter.clear();
                                    mAdapter.addAll(data.getSelected());
                                    mAdapter.notifyDataSetChanged();

                                    dialog.dismiss();
                                    if (data.getSelected().size() == 0) {
                                        mListView.setVisibility(View.INVISIBLE);
                                        mEmptyText.setText(R.string.empty_subscriptions);
                                        mEmptyText.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                            //Cancel Button
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
        /**Allows the user to add a RSS Feed using Alert Dialog */
        Button addButton = (Button) findViewById(R.id.buttonAddCat);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Subscription.this);
                dialogBuilder.setTitle("Manage Subscriptions");
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, data.getAvailable()) {

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
                        data.getSelected().add(data.getAvailable().get(which));
                        data.setSelected(data.getAvailable().get(which).toString());

                        mAdapter.clear();
                        mAdapter.addAll(data.getSelected());
                        mAdapter.notifyDataSetChanged();

                        adapter.clear();
                        adapter.addAll(data.getAvailable());
                        adapter.notifyDataSetChanged();

                        mListView.setVisibility(View.VISIBLE);
                        mEmptyText.setText(R.string.selected);
                        mEmptyText.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });
                dialogBuilder.setNeutralButton("Add Custom RSS Feed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Subscription.this);
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

                                                    if (data.getSelected().size() == 0) {
                                                        mEmptyText.setText(R.string.selected);
                                                        mEmptyText.setVisibility(View.VISIBLE);
                                                    }

                                                    data.getSelected().add(title);
                                                    data.setSelected(title);

                                                    mAdapter.clear();
                                                    mAdapter.addAll(data.getSelected());
                                                    mAdapter.notifyDataSetChanged();
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