package com.example.vovch.listogram_20;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class NewGroup extends WithLoginActivity {
    private NewGroup.CheckUserTask chTask;
    private NewGroup.NewGroupMakerTask ngTask;
    protected int ADDED_USERS_BIG_NUMBER = 400000;
    protected int NumberOfLines = 0;
    private int newGroupId;
    private String userId;
    private String groupName;
    protected boolean stepOneDone = false;
    protected ArrayList <Integer> AddedUsersIds = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        userId = getIntent().getExtras().getString("userId");
        AddedUsersIds.add(AddedUsersIds.size(), Integer.parseInt(userId));
        Button addUserButton = (Button)findViewById(R.id.newgroupadduserbutton);
        addUserButton.setClickable(true);
        View.OnClickListener addUserButtonListenner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        };
        addUserButton.setOnClickListener(addUserButtonListenner);
        Button confirmGroupAdding = (Button)findViewById(R.id.newgroupsubmitbutton);
        confirmGroupAdding.setClickable(true);
        View.OnClickListener confirmListenner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAdding();
            }
        };
        confirmGroupAdding.setOnClickListener(confirmListenner);
    }
    private void addUser(){
        int id = getAddingUserId();
        boolean userAlreadyAdded = false;
        if(AddedUsersIds.contains(id))
        {
            userAlreadyAdded = true;
        }
        if(userAlreadyAdded){
            TextView erView = (TextView) findViewById(R.id.newgrouperrorreporter);
            erView.setText("User Already Added");
        }
        else {
            checkUser(id);
        }
    }
    private int getAddingUserId(){
        TextView newUserIdTextView = (TextView)findViewById(R.id.newgroupnewuseridtextview);
        int id = Integer.parseInt(newUserIdTextView.getText().toString());
        return id;
    }
    private void checkUser(int id){
        chTask = new CheckUserTask(String.valueOf(id), " ", "checkuser");
        chTask.work();
    }
    protected void addNewUserToArray(String name){
        TextView newUserTextView = (TextView) findViewById(R.id.newgroupnewuseridtextview);
        AddedUsersIds.add(AddedUsersIds.size(), Integer.parseInt(newUserTextView.getText().toString()));
    }
    protected void drawnewUserLayout(String name){
        int matchParent = LinearLayout.LayoutParams.MATCH_PARENT;
        LinearLayout.LayoutParams addedUserButtonParameters = new LinearLayout.LayoutParams(matchParent, 180);
        LinearLayout addingUsersLayout = (LinearLayout) findViewById(R.id.newgroupaddeduserslayout) ;
        Button newUserButton = new Button(addingUsersLayout.getContext());
        newUserButton.setLayoutParams(addedUserButtonParameters);
        newUserButton.setGravity(Gravity.CENTER_HORIZONTAL);
        newUserButton.setText(name);
        newUserButton.setId(ADDED_USERS_BIG_NUMBER + NumberOfLines);

        NumberOfLines++;
        newUserButton.setLongClickable(true);
        View.OnLongClickListener addedUserListenner = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int id = v.getId();
                AddedUsersIds.remove(findViewById(id));
                AddedUsersIds.trimToSize();
                findViewById(id).setVisibility(View.GONE);
                return false;
            }
        };
        newUserButton.setOnLongClickListener(addedUserListenner);
        addingUsersLayout.addView(newUserButton);
    }
    protected void confirmAdding(){
        EditText eText = (EditText)findViewById(R.id.newgroupnameview);
        groupName = eText.getText().toString();
        if(!groupName.equals("")) {
            addGroup(groupName);
        }
        else{
            TextView erText = (TextView) findViewById(R.id.newgrouperrorreporter);
            erText.setText("Enter Your Group Name");
        }
    }
    protected void addGroup(String name){
        String usersString = makeUsersString();
        ngTask = new NewGroupMakerTask(name, usersString, "newgroup");
        ngTask.work();
    }
    protected String makeUsersString(){
        StringBuilder result = new StringBuilder("");
        for(int i = 0;i < AddedUsersIds.size();i++){
            result.append(AddedUsersIds.get(i).toString() + '\0');
        }
        return result.toString();
    }
    protected void setGroupId(int id){
        newGroupId = id;
    }
    public WithLoginActivity.FirstLoginAttemptTask getFirstLoginAttemptTask(){
        if(!stepOneDone) {
            return chTask;
        }
        else  {
            return ngTask;
        }
    }
    protected class CheckUserTask extends FirstLoginAttemptTask{
        CheckUserTask(String username, String userpassword, String action){
            super(username, userpassword, action);
        }
        @Override
        protected void onGoodResult(String result){
            addNewUserToArray(result);
            drawnewUserLayout(result);
        }
        @Override
        protected void onBedResult(String result){
            TextView tView = (TextView)findViewById(R.id.newgrouperrorreporter);
            tView.setText(result);
        }
    }
    protected class NewGroupMakerTask extends FirstLoginAttemptTask{
        NewGroupMakerTask(String username, String userpassword, String action){
            super(username, userpassword, action);
            stepOneDone = true;
        }
        @Override
        protected void onGoodResult(String result){
            setGroupId(Integer.parseInt(result));
            Intent intent = new Intent(NewGroup.this, Group2Activity.class);
            intent.putExtra("groupid", String.valueOf(newGroupId));
            EditText groupNameEditText = (EditText)findViewById(R.id.newgroupnameview);
            intent.putExtra("name", groupNameEditText.getText().toString());
            intent.putExtra("userid", userId);
            startActivity(intent);
        }
        @Override
        protected void onBedResult(String result){

        }
    }
}

