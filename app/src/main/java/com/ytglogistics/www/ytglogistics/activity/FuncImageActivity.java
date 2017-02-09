package com.ytglogistics.www.ytglogistics.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.github.library.listener.OnRecyclerItemClickListener;
import com.ytglogistics.www.ytglogistics.MyApplication;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.been.DataCbm;
import com.ytglogistics.www.ytglogistics.been.DataImg;
import com.ytglogistics.www.ytglogistics.utils.Consts;
import com.ytglogistics.www.ytglogistics.utils.FileUtils;
import com.ytglogistics.www.ytglogistics.utils.PublicWay;
import com.ytglogistics.www.ytglogistics.utils.WWToast;
import com.ytglogistics.www.ytglogistics.xutils.WWXCallBack;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/23.
 */

public class FuncImageActivity extends FatherActivity {
    @BindView(R.id.et_no)
    EditText etNo;
    @BindView(R.id.lv_data)
    RecyclerView lvData;
    @BindView(R.id.tv_newfilename)
    EditText tvNewfilename;
    @BindView(R.id.tv_select)
    TextView tvSelect;
    @BindView(R.id.tv_takephoto)
    TextView tvTakephoto;
    @BindView(R.id.tv_del)
    TextView tvDel;
    @BindView(R.id.tv_commit)
    TextView tvCommit;
    @BindView(R.id.ll_operate)
    LinearLayout llOperate;
    private ArrayList<DataImg> lsit = new ArrayList<DataImg>();
    private BaseRecyclerAdapter mAdapter;
    private int selectPos = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.act_funcimage;
    }

    @Override
    protected void initValues() {
        initDefautHead("现场图片", true);
    }

    @Override
    protected void initView() {
        mAdapter = new BaseRecyclerAdapter<DataImg>(this, lsit, R.layout.list_tv_two_item) {
            @Override
            protected void convert(BaseViewHolder helper, DataImg item) {
                helper.setText(R.id.tv_0, item.fName);
                helper.setText(R.id.tv_1, item.Status);
                if(item.isSelect){
                    helper.getView(R.id.ll_container).setBackgroundResource(R.color.top_title_bg);
                }else{
                    helper.getView(R.id.ll_container).setBackgroundResource(R.color.white);
                }
            }
        };
        mAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectPos = position;
                DataImg item = (DataImg) mAdapter.getItem(position);
                tvNewfilename.setText(item.fName);
                for (int i = 0; i <mAdapter.getData().size() ; i++) {
                    ((DataImg) mAdapter.getItem(i)).isSelect=false;
                }
                ((DataImg) mAdapter.getItem(position)).isSelect=true;
                mAdapter.notifyDataSetChanged();
            }
        });
        lvData.setHasFixedSize(true);
        lvData.setLayoutManager(new LinearLayoutManager(this));
        lvData.setItemAnimator(new DefaultItemAnimator());
        mAdapter.openLoadAnimation(false);
        lvData.setAdapter(mAdapter);
        tvNewfilename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (selectPos == -1) {
                    } else {
                        ((DataImg) mAdapter.getItem(selectPos)).fName = s + "";
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    protected void doOperate() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_select, R.id.tv_takephoto, R.id.tv_del, R.id.tv_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_select:
                PublicWay.startImageSelectActivity(this, 888, false, false,
                        ImageSelectActivity.MODE_PHOTO_ALBUM);
                break;
            case R.id.tv_takephoto:
                PublicWay.startImageSelectActivity(this, 888, false, false,
                        ImageSelectActivity.MODE_TAKE_PICTURE);
                break;
            case R.id.tv_del:
                if (selectPos == -1) {
                    WWToast.showShort("请先选择一条记录");
                } else {
                    mAdapter.remove(selectPos);
                    tvNewfilename.setText("");
                    selectPos = -1;
                }
                break;
            case R.id.tv_commit:
                String inp_path = etNo.getText().toString().trim();

                if (TextUtils.isEmpty(inp_path)) {
                    WWToast.showShort("请输入存放的目录名");
                    return;
                }
                if (mAdapter.getData() != null && mAdapter.getData().size() > 0) {
                    for (int i = 0; i < mAdapter.getData().size(); i++) {
                        DataImg item = (DataImg) mAdapter.getItem(i);
                        if (item.Status.equals("待上传")) {
                            upLocalImg(i, item, inp_path);
                        }
                    }
                } else {
                    WWToast.showShort("没有任何图片文件");
                    return;
                }

                break;
        }
    }

    protected void upLocalImg(final int pos, final DataImg item, String inp_path) {
        showWaitDialog();
        RequestParams params = new RequestParams("http://www.yplog.com.cn/Wcf/bllService.svc/UploadPicture/" + MyApplication
                .getInstance().getSessionId() + "/" + Uri.encode(inp_path, "utf-8"));
        String newUrl = FileUtils.getCompressedImageFileUrl(item.ImageUrl);
        params.addBodyParameter("file", new File(newUrl), null, item.fName);
        params.setMultipart(true);
        x.http().post(params, new WWXCallBack("UploadPicture") {

            @Override
            public void onAfterSuccessOk(JSONObject data) {
                ((DataImg) mAdapter.getItem(pos)).Status = "已上传";
                mAdapter.notifyDataSetChanged();
//                if(pos==(mAdapter.getData().size()-1)){
                  WWToast.showShort(item.fName+" 上传成功");
//                }
            }

            @Override
            public void onAfterSuccessError(JSONObject data) {
                super.onAfterSuccessError(data);
                ((DataImg) mAdapter.getItem(pos)).Status = "上传失败";
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAfterFinished() {
                dismissWaitDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || data != null) {
            switch (requestCode) {
                case 888:
                    DataImg item = new DataImg();
                    item.ImageUrl = data.getStringExtra(Consts.KEY_DATA);
                    item.fName = getFileName(item.ImageUrl);
                    item.Status = "待上传";
                    mAdapter.add(item);
                    break;
                default:
                    break;
            }
        }
    }

    public String getFileName(String pathandname){

        int start=pathandname.lastIndexOf("/");
        int end=pathandname.lastIndexOf(".");
        if(start!=-1 && end!=-1){
            return pathandname.substring(start+1,end);
        }else{
            return null;
        }

    }
}
