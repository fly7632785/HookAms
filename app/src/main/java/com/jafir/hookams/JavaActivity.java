package com.jafir.hookams;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by jafir on 2017/12/28.
 */

public class JavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, OtherActivity.class));
        OtherActivity.Person person = new OtherActivity.Person("jafir",11);
        Class c = person.getClass();
        Class cc = OtherActivity.Person.class;
    }
}
