package com.ajibadedah.superherocharacters.firebase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ajibadedah.superherocharacters.CustomImageView;
import com.ajibadedah.superherocharacters.R;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ajibadedah.superherocharacters.DetailFragment.ARG_ON_LONG_CLICK_TEXT;
import static com.ajibadedah.superherocharacters.DetailFragment.ARG_ON_LONG_CLICK_URL;


public class ChatActivity extends AppCompatActivity {


    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 150;

    public static final int RC_SIGN_IN = 1;

    private ListView mChatListView;
    private FirebaseRecyclerAdapter mChatAdapter;
    private ProgressBar mProgressBar;
    private EditText mChatEditText;
    private Button mSendButton;

    private String mUsername;

    private DatabaseReference mChatDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private String detailChat;
    private String detailUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        detailChat = getIntent().hasExtra(ARG_ON_LONG_CLICK_TEXT) ?
                getIntent().getStringExtra(ARG_ON_LONG_CLICK_TEXT) : null;

        detailUrl = getIntent().hasExtra(ARG_ON_LONG_CLICK_URL) ?
                getIntent().getStringExtra(ARG_ON_LONG_CLICK_URL) : null;

        mUsername = ANONYMOUS;

        // Initialize Firebase components
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mChatDatabaseReference = mFirebaseDatabase.getReference().child("messages");

//        mChatListView = (ListView) findViewById(R.id.chatListView);
        mChatEditText = (EditText) findViewById(R.id.chatEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);

        // Initialize message ListView and its adapter
//        List<Chat> friendlyMessages = new ArrayList<>();
//        mChatAdapter = new ChatAdapter(this, R.layout.chat_item, friendlyMessages);
//        mChatListView.setAdapter(mChatAdapter);

        // Enable Send button when there's text to send
        mChatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mChatEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chat chat = new Chat(detailChat, mChatEditText.getText().toString(), mUsername, detailUrl);
                mChatDatabaseReference.push().setValue(chat);

                // Clear input box
                mChatEditText.setText("");
                detailChat = null;
                detailUrl = null;
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        mChatAdapter.cleanup();
        detachDatabaseReadListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onSignedInInitialize(String username) {
        mUsername = username;
        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        RecyclerView recycler = (RecyclerView) findViewById(R.id.chatRecyclerView);
        recycler.setHasFixedSize(false);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        mChatAdapter = new FirebaseRecyclerAdapter<Chat, ChatHolder>(Chat.class, R.layout.chat_item, ChatHolder.class, mChatDatabaseReference) {
            @Override
            public void populateViewHolder(ChatHolder chatMessageViewHolder, Chat chatMessage, int position) {
                chatMessageViewHolder.setName(chatMessage.getName());
                chatMessageViewHolder.setMessage(chatMessage.getTextChat());
                chatMessageViewHolder.setReferenceField(chatMessage.getTextReference());
                chatMessageViewHolder.setThumbnail(chatMessage.getPhotoUrl());

            }
        };
        recycler.setAdapter(mChatAdapter);

//        if (mChildEventListener == null) {
//            mChildEventListener = new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    Chat chat = dataSnapshot.getValue(Chat.class);
//                    mChatAdapter.add(chat);
//                }
//
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
//                public void onChildRemoved(DataSnapshot dataSnapshot) {}
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
//                public void onCancelled(DatabaseError databaseError) {}
//            };
//            mChatDatabaseReference.addChildEventListener(mChildEventListener);
//        }
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        mChatAdapter.cleanup();
        detachDatabaseReadListener();
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mChatDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    private static class ChatHolder extends RecyclerView.ViewHolder {
        private final CustomImageView mThumbnail;
        private final TextView mNameField;
        private final TextView mMessageField;
        private final TextView mReferenceField;


        public ChatHolder(View itemView) {
            super(itemView);
            mThumbnail = (CustomImageView) itemView.findViewById(R.id.photoImageView);
            mNameField = (TextView) itemView.findViewById(R.id.nameTextView);
            mMessageField = (TextView) itemView.findViewById(R.id.messageTextView);
            mReferenceField = (TextView) itemView.findViewById(R.id.referenceTextView);
        }

        public void setName(String name) {
            mNameField.setText(name);
        }

        public void setMessage(String message) {
            mMessageField.setText(message);
        }

        public void setReferenceField(String reference) {
            if (reference == null) return;
            mReferenceField.setText(reference);
            mReferenceField.setVisibility(View.VISIBLE);
        }

        public void setThumbnail(String thumbnail) {
            if (thumbnail == null) return;
            Picasso.with(itemView.getContext())
                    .load(thumbnail)
                    .into(mThumbnail);
            mThumbnail.setVisibility(View.VISIBLE);
        }
    }
}
