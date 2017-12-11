package com.combintech.baojili.activity.itemout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.combintech.baojili.R;
import com.combintech.baojili.activity.base.BaseActivity;
import com.combintech.baojili.constant.RequestCode;
import com.combintech.baojili.fragment.itemout.UpdateItemOutFragment;
import com.combintech.baojili.model.ItemInOut;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.DeleteItemTrPresenter;
import com.combintech.baojili.util.ErrorUtil;

public class UpdateItemOutActivity extends BaseActivity implements
        UpdateItemOutFragment.OnUpdateItemOutFragmentListener, DeleteItemTrPresenter.DeleteItemTrView {

    public static final String EXTRA_TR_ITEM = "x_tr_item";

    private DeleteItemTrPresenter deleteItemTrPresenter;

    private ItemInOut itemInOut;

    public static void start(Activity activity, ItemInOut itemInOut) {
        Intent intent = new Intent(activity, UpdateItemOutActivity.class);
        intent.putExtra(EXTRA_TR_ITEM, itemInOut);
        activity.startActivityForResult(intent,RequestCode.UPDATE_ITEM_OUT_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itemInOut = getIntent().getParcelableExtra(EXTRA_TR_ITEM);
        deleteItemTrPresenter = new DeleteItemTrPresenter(this);

        setContentView(R.layout.activity_base_fragment);

        setUpToolbar();
        if (savedInstanceState == null) {
            replaceFragment(UpdateItemOutFragment.newInstance(), false, UpdateItemOutFragment.TAG);
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = toolbar.findViewById(R.id.tv_title);
        tvTitle.setText(getTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_delete){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
                    .setTitle(getString(R.string.delete_transaction))
                    .setMessage(getString(R.string.this_action_cannot_be_undone))
                    .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            showProgressDialog();
                            String itemTrIdToDelete = itemInOut.getTrItemID();
                            deleteItemTrPresenter.deleteItemTr(itemTrIdToDelete);
                        }
                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            // no op, just dismiss
                        }
                    });
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        deleteItemTrPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        deleteItemTrPresenter.onPause();
    }

    private UpdateItemOutFragment getUpdateItemInFragment() {
        return (UpdateItemOutFragment) getSupportFragmentManager().findFragmentByTag(UpdateItemOutFragment.TAG);
    }

    @Override
    public void onSuccessUpdateItemOut() {
        setResult(Activity.RESULT_OK);
        this.finish();
    }

    @Override
    public void onErrorDeleteItemTr(Throwable throwable) {
        hideProgressDialog();
        ErrorUtil.showToast(this, throwable);
    }

    @Override
    public void onSuccessDeleteItemTr(MessageResponse messageResponse) {
        hideProgressDialog();
        ErrorUtil.showToast(this, messageResponse.getMessage());
        setResult(Activity.RESULT_OK);
        this.finish();
    }
}
