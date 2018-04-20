package com.example.maola.degradotourmap.User;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.maola.degradotourmap.Model.Comment;
import com.example.maola.degradotourmap.R;
import com.example.maola.degradotourmap.Utility.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnCommentFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CommentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    static final String VAR_ID_MARKER = "varIdMarker";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.comment_recView)
    RecyclerView commentRecView;
    @BindView(R.id.comment_img_mypicture)
    ImageView commentImgMypicture;
    @BindView(R.id.comment_edt_mycomment)
    EditText commentEdtMycomment;
    @BindView(R.id.comment_btn_publish)
    Button commentBtnPublish;
    Unbinder unbinder;
    private String mComment;
    private DatabaseReference mDatabaseReferences;
    private Comment comment;
    private FirebaseUser user;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnCommentFragmentInteractionListener mListener;

    public CommentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommentFragment newInstance(String param1, String param2) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putString(VAR_ID_MARKER, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(VAR_ID_MARKER);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        unbinder = ButterKnife.bind(this, view);

//        mDatabaseReferences = FirebaseUtils.getCommentsRef();
//        user = FirebaseAuth.getInstance().getCurrentUser();

//        Bundle bundle = this.getArguments();
//
//        if (bundle != null) {
//            mParam2 = bundle.getString("varIdMarker");
//        }

        commentEdtMycomment.setText(mParam1+"prova");

        commentBtnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadComment();
            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void uploadComment() {
        if (mListener != null) {
            mListener.onFragmentInteraction(commentEdtMycomment.getText().toString());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCommentFragmentInteractionListener) {
            mListener = (OnCommentFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCommentFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

//    @OnClick(R.id.comment_btn_publish)
//    public void onViewClicked() {
//        mComment = commentEdtMycomment.getText().toString();
//        Date currentTime = Calendar.getInstance().getTime();
//
//        comment = new Comment(user.getEmail(), user.getDisplayName(), mComment, currentTime.toString());
//
//        String commentID = mDatabaseReferences.push().getKey();
//        mDatabaseReferences.child(commentID).setValue(comment);
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCommentFragmentInteractionListener {
        void onFragmentInteraction(String text);
    }
}
