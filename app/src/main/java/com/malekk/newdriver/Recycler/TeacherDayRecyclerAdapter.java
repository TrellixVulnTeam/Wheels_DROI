package com.malekk.newdriver.Recycler;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malekk.newdriver.DataSorce.TeacherDayDataSorce;
import com.malekk.newdriver.MainActivity;
import layout.MyStudents;
import com.malekk.newdriver.R;
import com.malekk.newdriver.models.Day;
import com.malekk.newdriver.models.Notification;
import com.malekk.newdriver.models.ScheduleDay;
import com.malekk.newdriver.models.lesson;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import layout.TeacherDay;
import layout.TeacherSchedule;

/**
 * Created by malekkbh on 13/11/2017.
 */

public class TeacherDayRecyclerAdapter extends  RecyclerView.Adapter<TeacherDayRecyclerAdapter.TeacherDayViewHolder> {


    private Context context;
    List<lesson> dayList = new ArrayList<>();
    private LayoutInflater inflater;
    LocalDateTime date ;

    Context applicationContext = MainActivity.getContextOfApplication();


     SharedPreferences ref = applicationContext.getSharedPreferences("USER_INFO" , 0) ;
     String teacherID  ;
     String UID ;
     String teacherStudent;
     String message ;
     String title ;
     String name  ;
     int  pos ;
     private Paint p = new Paint();

     RecyclerView rv ;

    private AlertDialog.Builder alertDialog;


    TeacherDayDataSorce today ;




    int i , j  ;
    private ProgressDialog progressDialog;


    //Constructor that takes the inflater.
    public TeacherDayRecyclerAdapter(final Context context , final LocalDateTime date ) {

       // this.ref = applicationContext.getSharedPreferences("USER_INFO" , 0) ;

        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.dayList = new ArrayList<>() ;
        this.date = date ;
        today = new TeacherDayDataSorce(date );

        View v = inflater.inflate(R.layout.fragment_student_teacher, null, false);
        rv = (RecyclerView) v.findViewById(R.id.rvTeacherDay)  ;


        teacherStudent = ref.getString("StudentTeacher" , "");
        teacherID = ref.getString("Teacher" , "" ) ;
        UID = ref.getString("UID" , "") ;
        name = ref.getString("Name" , "") ;


        initSwipe();

        fillUpDayList( ref.getInt(MainActivity.USER_STAGE , 0));
        //initDialog();
     // final TeacherDayDataSorce today = new TeacherDayDataSorce(date) ;


//        if ( dayList.size() == 0 ){
//            today.getDayOfTheWeek(new TeacherDayDataSorce.DayOfTheWeekArrived() {
//                @Override
//                public void data(ScheduleDay dayData) {
//                    ScheduleDay newScheduleDay = dayData;
//                    Day newDay = new Day(newScheduleDay) ;
//                    dayList = newDay.daySchedule ;
//                    TeacherDayRecyclerAdapter.this.notifyDataSetChanged();
//
//
//                }
//            });
//        }
        System.out.println("***** FUCK con' ");
    }//con'


    public void fillUpDayList(final int stage){
        today.getTeacherDay(new TeacherDayDataSorce.TeacherDayDataArrived() {
            @Override
            public void data(List<lesson> data) {
                TeacherDayRecyclerAdapter.this.dayList = data;
                TeacherDayRecyclerAdapter.this.notifyDataSetChanged();
                System.out.println("**** +" + dayList.size());

                if (context.getSharedPreferences("re" , Context.MODE_PRIVATE).getInt("mChange" , 0) == 2 && dayList.size() != 0 ){
                    replaceStudents();
                }

                if ( dayList.size() == 0 ){

                    ref.edit().putInt(MainActivity.USER_STAGE , 2301).commit() ;

                    today.getDayOfTheWeek( context ,new TeacherDayDataSorce.DayOfTheWeekArrived() {
                        @Override
                        public void data(ScheduleDay dayData) {
                            ScheduleDay newScheduleDay = new ScheduleDay();
                            Day newDay  ;

                            if (dayData != null ) {

                                newScheduleDay = dayData;
                                newDay = new Day(newScheduleDay , date) ;
                                dayList = newDay.daySchedule;

                                ref.edit().putInt(MainActivity.USER_STAGE , MainActivity.TEACHER_DAY).commit() ;


                            }

//                            else {
//                                System.out.println("**********FUCK !!! NO DAY ");
//                            }

                            else {
//                                if (teacherStudent.equals("Student")) {
//                                    message = "Please contact Your teacher" ;
//                                    title = "No Schedul for This Day !" ;
//                                }
//                                else
//                                {
//                                    message = "Please edit your Schedule , press 'OK' to edit " ;
//                                    title = "No Schedul for This Day !" ;
//                                }
//
//                                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                                final AlertDialog dialogNoSchedule = builder.show();
//
//                                builder.setMessage(message)
//                                        .setTitle(title)
//                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                if (teacherStudent.equals("Teacher") ) {
//                                                    FragmentActivity activity = (FragmentActivity) context;
//                                                    activity.getSupportFragmentManager().beginTransaction()
//                                                            .replace(R.id.container, new TeacherSchedule()).commit();
//                                                    dialogNoSchedule.dismiss();
//                                                    //  ((FragmentActivity) context).finish();
//                                                }
//                                            }
//                                        });
//                                if( stage == MainActivity.TEACHER_DAY)
//                                builder.show().show();

                                if( stage == MainActivity.TEACHER_DAY)
                                noSchedulDialog();

                            }

                            TeacherDayRecyclerAdapter.this.notifyDataSetChanged();

                            System.out.println("***** +" + dayList.size() + "day : " + newScheduleDay.toString() );
                        }
                    });



                }//if
            }
        });

    }




    public void noSchedulDialog() {

//        final ProgressDialog pd = new ProgressDialog(context) ;
//        pd.show();

        if (teacherStudent.equals("Student")) {
            message = "Please contact Your teacher" ;
            title = "No Schedul for This Day !" ;
        }
        else
        {
            message = "Please edit your Schedule , press 'OK' to edit " ;
            title = "No Schedul for This Day !" ;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (teacherStudent.equals("Teacher")) {
                            FragmentActivity activity = (FragmentActivity) context;
                            activity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, new TeacherSchedule()).commit();
                           // pd.dismiss();
                            dialog.dismiss();
                           // ((FragmentActivity) context).finish();
                        }
                    }
                });
        final AlertDialog dialogNoSchedule = builder.show();

        dialogNoSchedule.show();

    }


    public  Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public TeacherDayRecyclerAdapter.TeacherDayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.teacher_day_model, parent, false);
        TeacherDayRecyclerAdapter.TeacherDayViewHolder holder = new TeacherDayRecyclerAdapter.TeacherDayViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(TeacherDayViewHolder h, int position) {
        lesson lessonPos = dayList.get(position) ;

        h.tvStart.setText(lessonPos.getSingleLesson().getStart()) ;
        h.tvEnd.setText(lessonPos.getSingleLesson().getEnd());
        h.tvAvibable.setText(lessonPos.getStudentName());
        h.tvDate.setText(date.toString("dd/MM/yy"));

        if(lessonPos.getStudentName().equals("free")  )
            h.imgTD.setImageResource(R.drawable.icons80_percents_filled);
        else
            h.imgTD.setImageResource(R.drawable.icons8_checked);

    }


    @Override
    public int getItemCount() {
        return dayList.size() ;
    }





    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                final int position = viewHolder.getAdapterPosition();
                TeacherDayRecyclerAdapter.this.pos = position ;

                if (direction == ItemTouchHelper.LEFT){

                    if( !TeacherDayRecyclerAdapter.this.dayList.get(position).getStudentName().equals(name)
                            && teacherStudent.equals("Student")) {
                        AlertDialog.Builder builderN = new AlertDialog.Builder(context);

                        builderN.setTitle("It's Not Your Lessone to cancel !")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        TeacherDayRecyclerAdapter.this.notifyDataSetChanged();
                                    }
                                });

                        builderN.show().show();
                    }
                    else { //
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Are sure you want to Cancel the lesson ?!");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                cancelStudent(TeacherDayRecyclerAdapter.this.dayList.get(position));
                                Toast.makeText(context, "Canceled!", Toast.LENGTH_LONG).show();
                            }

                        })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        builder.show().dismiss();
                                        TeacherDayRecyclerAdapter.this.notifyDataSetChanged();

                                    }
                                });

                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                TeacherDayRecyclerAdapter.this.notifyDataSetChanged();
                            }
                        }) ;

                        builder.show().show();
                    }

                } else { // diriction swip right
                    if (ref.getString(MainActivity.USER_STUDENT_TEACHER ,"").equals("Teacher"))
                                 initDialog();
                    else
                        studentPickLesson(dayList.get(pos));

                }
            }




            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {



                Bitmap icon;


                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    String studentName = ref.getString(MainActivity.USER_NAME, "");
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0 ) {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        Drawable d = context.getResources().getDrawable(R.drawable.icons8_edit_filled);
                        icon = TeacherDayRecyclerAdapter.this.drawableToBitmap(d);
                        //icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.fui_done_check_mark);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        Drawable d = context.getResources().getDrawable(R.drawable.icons8_delete_sign);

                        icon = TeacherDayRecyclerAdapter.this.drawableToBitmap(d);
                        //BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_camera);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }

                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(TeacherDay.vr);


    }

    private void initDialog( ){

        final AlertDialog.Builder alertbuilder = new AlertDialog.Builder(context);

//        FragmentActivity activity = (FragmentActivity) context ;
//        View view = activity.getLayoutInflater().inflate(R.layout.teacher_day_model,null);

        FragmentActivity activity = (FragmentActivity) context;
        final View view = activity.getLayoutInflater().inflate(R.layout.teacher_day_dialog, null, false);


        Button teacherBreakBtn = (Button) view.findViewById(R.id.btnTeacherBreak) ;
        final Button replaceStudentBtn = (Button) view.findViewById(R.id.btnReplaceStudent) ;
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel) ;

        alertbuilder.setView(view) ;

        final AlertDialog alert1 = alertbuilder.show() ;

        alert1.getWindow().setBackgroundDrawableResource(R.color.fui_transparent);

        teacherBreakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teacherBreak(TeacherDayRecyclerAdapter.this.dayList.get(TeacherDayRecyclerAdapter.this.pos));
                alert1.dismiss();
            }
        });

        replaceStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSharedPreferences("re" , Context.MODE_PRIVATE).edit().putInt("mChange" , 0 ).commit() ;
                replaceStudents();
               alert1.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert1.dismiss();
                TeacherDayRecyclerAdapter.this.notifyDataSetChanged();
            }
        });


        alert1.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                TeacherDayRecyclerAdapter.this.notifyDataSetChanged();
            }
        });


        alert1.show();

    } // inite right dialog

    public void teacherBreak ( lesson mLesson ) {

        lesson abc = mLesson ;

        String cancelledID = abc.getId() ;

        FirebaseDatabase.getInstance().getReference()
                .child("Day").child(teacherID)
                .child(String.valueOf(date.year().get()))
                .child(String.valueOf(date.getMonthOfYear()))
                .child(String.valueOf(date.getDayOfMonth()))
                .child(abc.toString())
                .removeValue() ;
        //remove lessone for this day if calss is taken
        if (!abc.getStudentName().equals("free")){
            FirebaseDatabase.getInstance().getReference().child("lessonsForThisDay").child(abc.getId()).child(date.toString("YYYY/M/d")).removeValue() ;
        }


        abc.setStudentName("Teacher Break");

        FirebaseDatabase.getInstance().getReference()
                .child("Day")
                .child(teacherID)
                .child(String.valueOf(date.year().get()))
                .child(String.valueOf(date.getMonthOfYear()))
                .child(String.valueOf(date.getDayOfMonth()))
                .child(abc.toString())
                .setValue(abc);

        TeacherDayRecyclerAdapter.this.notifyDataSetChanged();

        Toast.makeText(context, "removed", Toast.LENGTH_LONG).show();

        Notification noti = new Notification( cancelledID , "Lesson Cancelled" ,
                "Youer lessone have been cancelled by your Teacher") ;

        FirebaseDatabase.getInstance().getReference().child("Notifications").push().child("notification").setValue(noti) ;

    }// teacherBreak

    public void replaceStudents (){

        // newStudent = spStudent.getSelectedItem().toString() ;
        SharedPreferences replaceRef = context.getSharedPreferences("re" , Context.MODE_PRIVATE) ;
        lesson abc = null;


        if ( context.getSharedPreferences("re" , Context.MODE_PRIVATE).getInt("mChange" , 0) == 0 ) {
            context.getSharedPreferences("re", Context.MODE_PRIVATE).edit().putInt("mChange", 1).commit();

            lesson ls = dayList.get(pos);

            replaceRef.edit().putString("oldName", ls.getStudentName()).commit();
            replaceRef.edit().putString("ls_tostring", ls.toString()).commit();
            replaceRef.edit().putString("date", date.toString("dd.MM.yy")).commit();
            replaceRef.edit().putInt("pos", pos).commit();
           // replaceRef.edit().putInt("mChange" , 1) ;


            FragmentActivity activity = (FragmentActivity) context;
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, new MyStudents()).commit();
        }

        else {
            if (replaceRef.getInt("mChange", 0) == 2) {

                for (lesson f : dayList) {
                    if (f.toString().equals(replaceRef.getString("ls_tostring", "")))
                        abc = f;
                }

             //  System.out.println("******: " + abc.toString());

                String newStudentID = replaceRef.getString("newID", "");
                String newStudentName = replaceRef.getString("newName", "");


                FirebaseDatabase.getInstance().getReference()
                        .child("Day")
                        .child(teacherID)
                        .child(String.valueOf(date.year().get()))
                        .child(String.valueOf(date.getMonthOfYear()))
                        .child(String.valueOf(date.getDayOfMonth()))
                        .child(replaceRef.getString("ls_tostring", ""))
                        .removeValue();

                String cancelledStudentID = abc.getId();

                abc.setStudentName(newStudentName);
                abc.setId(newStudentID);

                DatabaseReference reff = FirebaseDatabase.getInstance().getReference()
                        .child("Day").child(teacherID)
                        .child(String.valueOf(date.year().get()))
                        .child(String.valueOf(date.getMonthOfYear()))
                        .child(String.valueOf(date.getDayOfMonth()))
                        .child(abc.toString());

                reff.setValue(abc);

                // reff.child("ID").setValue(newStudentID);

                TeacherDayRecyclerAdapter.this.notifyDataSetChanged();

                replaceRef.edit().putInt("mChange", 0).commit();



                //notification
//
                Notification newStudentNotification = new Notification(abc.getId(), "Your lessone is aproved",
                        "you have been singed to lesson at : " + abc.getSingleLesson().getStart() + "0n: " + date.toString("dd.MM.yy"));

                Notification cancelledLesson = new Notification(cancelledStudentID, "Your lesson have been cancelld",
                        "Your lesson at: " + abc.getSingleLesson().getStart() + "on: " + date.toString("dd.MM.yy") + "have been cancelled");

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Notifications");
                ref.push().child("notification").setValue(newStudentNotification);
                ref.push().child("notification").setValue(cancelledLesson);

            }
        }

    }// end replaceStudent

    public void cancelStudent (lesson ls ) {
        lesson abc = ls ;

        String cancelledID = abc.getId() ;

        FirebaseDatabase.getInstance().getReference()
                .child("Day")
                .child(teacherID)
                .child(String.valueOf(date.year().get()))
                .child(String.valueOf(date.getMonthOfYear()))
                .child(String.valueOf(date.getDayOfMonth()))
                .child(abc.toString())
                .removeValue() ;

        String cancelledStudentID = abc.getId() ;
        abc.setStudentName("free");
        abc.setId(" ");

        DatabaseReference reff = FirebaseDatabase.getInstance().getReference()
                .child("Day").child(teacherID)
                .child(String.valueOf(date.year().get()))
                .child(String.valueOf(date.getMonthOfYear()))
                .child(String.valueOf(date.getDayOfMonth()))
                .child(abc.toString()) ;

                 reff.setValue(abc);




      //  rv.removeViewAt(pos);
       // TeacherDayRecyclerAdapter.this.notifyItemRangeChanged(pos , dayList.size());

        TeacherDayRecyclerAdapter.this.notifyDataSetChanged();

        FirebaseDatabase.getInstance().getReference().child("lessonsForThisDay").child(abc.getId()).child(date.toString("YYYY/M/d"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            int a = dataSnapshot.getValue(Integer.class);
                            if (a > 0) {
                                dataSnapshot.getRef().setValue(a - 1);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        Notification noti = null ;
        if ( ref.getString(MainActivity.USER_STUDENT_TEACHER , "").equals("Teacher")) {
             noti = new Notification(cancelledID, "Lesson Cancelled",
                    "Youer lessone have been cancelled by your Teacher");
        }

        else {
            noti = new Notification(teacherID , "Lesson Cancelled" , ref.getString(MainActivity.USER_NAME , "") + " have Canceled lesson at :" +
                    abc.getSingleLesson().getStart() + "-" + abc.getSingleLesson().getEnd()  )  ;
        }

        FirebaseDatabase.getInstance().getReference("/Notifications").push().child("notification").setValue(noti) ;



    } //cancelStudent


    private void studentPickLesson(final lesson lesson) {

        final int[] lessonsForThisDay = new int[1];


        progressDialog = new ProgressDialog(context);

        progressDialog.show();
        FirebaseDatabase.getInstance().getReference().child("lessonsForThisDay").child(UID).child(date.toString("YYYY/M/d"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        if (dataSnapshot.exists()){
                           lessonsForThisDay[0] = Integer.valueOf(dataSnapshot.getValue().toString()) ;
                        }
                        else
                            lessonsForThisDay[0] = 0 ;

                        if ( lessonsForThisDay[0] > 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                            builder.setTitle("You have Singed a lesson for this Day")
                                    .setMessage("we will love to sign you for another lesson at any other day :)")
                                    .setPositiveButton("OK" , null) ;
                            builder.show().show();
                        } else{
                            if ( lesson.getStudentName().equals("free")) {


                                DatabaseReference ref123 = FirebaseDatabase.getInstance().getReference().child("Day").child(teacherID).child(date.toString("YYYY/M/d"));

                                ref123.child(lesson.toString()).removeValue();

                                lesson.setStudentName(ref.getString(MainActivity.USER_NAME, ""));
                                lesson.setId(ref.getString(MainActivity.USER_UID, ""));

                                ref123.child(lesson.toString()).setValue(lesson);

                                dataSnapshot.getRef().setValue(lessonsForThisDay[0] +1) ;

                                FirebaseDatabase.getInstance().getReference().child("Notifications").push().child("notification").setValue(
                                        new Notification(teacherID , "Class is Assigned" , lesson.getStudentName() + " have Assigned for  lesson at :" +
                                      lesson.getSingleLesson().getStart() + "-" + lesson.getSingleLesson().getEnd()  ) ) ;


                                TeacherDayRecyclerAdapter.this.notifyDataSetChanged();

                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context) ;

                                builder.setTitle("This lesson is unavailable").setMessage("please pick another lesson")
                                        .setNeutralButton("OK" , null) ;

                                builder.show().show();
                            }


                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        TeacherDayRecyclerAdapter.this.notifyDataSetChanged();

    }



    class TeacherDayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView tvStart , tvEnd , tvAvibable , tvDate;
        List<String> studentsList = new ArrayList<String>();
        ImageView imgTD ;


        //int i , j ;

        public TeacherDayViewHolder(View v) {
            super(v);

            tvStart = (TextView) v.findViewById(R.id.tvStart);
            tvEnd = (TextView) v.findViewById(R.id.tvEnd);
            tvAvibable = (TextView) v.findViewById(R.id.tvAvibable);
            tvDate = (TextView) v.findViewById(R.id.tvDate) ;
            imgTD = (ImageView) v.findViewById(R.id.imgTD) ;

            v.setOnClickListener(this);
            v.setOnLongClickListener(this);


        }
        
        

//        @Override
//        public void onClick(View v) {
//
//
//            final int pos = getAdapterPosition();
//
//            if(TeacherDayRecyclerAdapter.this.teacherStudent.equals("Teacher")) {
//
//
//                FragmentActivity activity = (FragmentActivity) context;
//
//                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//
//                final AlertDialog alertDialog = builder.show();
//
//
//                final View dialogView = activity.getLayoutInflater().inflate(R.layout.teacher_day_dialog, null, false);
//
//                final CheckBox checkBox  = (CheckBox) dialogView.findViewById(R.id.checkBox) ;
//                final Spinner spStudent = (Spinner) dialogView.findViewById(R.id.spStudents) ;
//                final ProgressBar progressBar = (ProgressBar) dialogView.findViewById(R.id.progressBar) ;
//
//
//                FirebaseDatabase.getInstance().getReference().child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        studentsList.clear();
//                        for (DataSnapshot child : dataSnapshot.getChildren()) {
//                            Profile profile = child.getValue(Profile.class);
//                            if(profile.getTeacher().equals(teacherID))
//                                studentsList.add(profile.getName());
//
//                        }
//
//                    }
//
//
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//
//
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>( activity,
//                        android.R.layout.simple_spinner_item, studentsList);
//
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//                spStudent.setAdapter(adapter);
//
//                spStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        String item = parent.getItemAtPosition(position).toString();
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//
//                    }
//                }) ;
//
//
//
//                Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancelLesson) ;
//
//                builder.setView(dialogView).setPositiveButton("Save", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        if ( checkBox.isChecked()){
//                            teacherBreak(dayList.get(pos));
////                            String cancelledID = abc.getId() ;
////
////                            FirebaseDatabase.getInstance().getReference()
////                                    .child("Day").child(teacherID)
////                                    .child(String.valueOf(date.year().get()))
////                                    .child(String.valueOf(date.getMonthOfYear()))
////                                    .child(String.valueOf(date.getDayOfMonth()))
////                                    .child(abc.toString()).removeValue() ;
////
////
////                            abc.setStudentName("Teacher Break");
////
////                            FirebaseDatabase.getInstance().getReference()
////                                    .child("Day")
////                                    .child(teacherID)
////                                    .child(String.valueOf(date.year().get()))
////                                    .child(String.valueOf(date.getMonthOfYear()))
////                                    .child(String.valueOf(date.getDayOfMonth()))
////                                    .child(abc.toString())
////                                    .setValue(abc);
////
////                            TeacherDayRecyclerAdapter.this.notifyDataSetChanged();
////
////                            System.out.println();
////
////                            Notification noti = new Notification( cancelledID , "Lesson Cancelled" ,
////                                    "Youer lessone have been cancelled by your Teacher") ;
////
////                            FirebaseDatabase.getInstance().getReference("/Notifications").push().child("notification").setValue(noti) ;
//                        }//end if checkBox is Checked
//
//
//                        else {
//                            //if checkBox is not Checked
//
//                            String newStudent = "" ;
//                                   newStudent = spStudent.getSelectedItem().toString() ;
//
//                            lesson abc = dayList.get(pos) ;
//
//                            FirebaseDatabase.getInstance().getReference()
//                                    .child("Day")
//                                    .child(teacherID)
//                                    .child(abc.toString())
//                                    .child(String.valueOf(date.year().get()))
//                                    .child(String.valueOf(date.getMonthOfYear()))
//                                    .child(String.valueOf(date.getDayOfMonth()))
//                                    .removeValue() ;
//
//                            String cancelledStudentID = abc.getId() ;
//                            abc.setStudentName(newStudent);
//                            abc.setId("");
//
//                           DatabaseReference reff = FirebaseDatabase.getInstance().getReference()
//                                    .child("Day").child(teacherID)
//                                    .child(String.valueOf(date.year().get()))
//                                    .child(String.valueOf(date.getMonthOfYear()))
//                                    .child(String.valueOf(date.getDayOfMonth()))
//                                    .child(abc.toString()) ;
//
//                                    reff.setValue(abc);
//                                    reff.child("ID").setValue(UID) ;
//
//                            TeacherDayRecyclerAdapter.this.notifyDataSetChanged();
//
//                            Notification newStudentNotification = new Notification( abc.getId() , "Your lessone is aproved" ,
//                     "you have been singed to lesson at : " + abc.getSingleLesson().getStart() + "0n: " + date.toString("dd.MM.yy")) ;
//
//                            Notification cancelledLesson = new Notification( cancelledStudentID ,"Your lesson have been cancelld" ,
//                                    "Your lesson at: " + abc.getSingleLesson().getStart() + "on: " + date.toString("dd.MM.yy") + "have been cancelled" ) ;
//
//                          DatabaseReference ref =  FirebaseDatabase.getInstance().getReference("/Notifications") ;
//                          ref.push().child("notification").setValue(newStudentNotification) ;
//                          ref.push().child("notification").setValue(cancelledLesson) ;
//
//                        }// end if checkBox is notChecked
//
//                    alertDialog.dismiss();
//                    }
//                })
//                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                            alertDialog.dismiss();
//                        }
//                    });
//                builder.show() ;
//
//            } // end if Teacher
//
//
//
//
//            if (TeacherDayRecyclerAdapter.this.teacherStudent.equals("Student") )
//            {
//
//                final lesson abc = dayList.get(pos);
//               // final String name = TeacherDayRecyclerAdapter.this.ref.getString("Name", "");
//
//                if( abc.getStudentName().equals("free")) {
//
//
//                    final DatabaseReference reff = FirebaseDatabase.getInstance().getReference()
//                            .child("Day")
//                            .child(teacherID)
//                            .child(String.valueOf(date.year().get()))
//                            .child(String.valueOf(date.getMonthOfYear()))
//                            .child(String.valueOf(date.getDayOfMonth()));
//
//                    reff.child(dayList.get(pos).toString()).removeValue();
//
//                    abc.setStudentName(name);
//                    abc.setId(UID);
//                    reff.child(abc.toString()).setValue(abc);
//
//
//                    final DatabaseReference profilerRef = FirebaseDatabase.getInstance().getReference().child("profile");
//
//                    profilerRef.getRef().child(teacherID).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Profile profile = dataSnapshot.getValue(Profile.class);
//                            TeacherDayRecyclerAdapter.this.i = profile.getLessons();
//                            i++;
//                            profilerRef.getRef().child(teacherID).child("lessons").setValue(i);
//                            TeacherDayRecyclerAdapter.this.notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
//
//                    profilerRef.getRef().child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//
//                            Profile profile = dataSnapshot.getValue(Profile.class);
//                            TeacherDayRecyclerAdapter.this.j = profile.getLessons();
//                            j++;
//                            profilerRef.getRef().child(UID).child("lessons").setValue(j);
//                            TeacherDayRecyclerAdapter.this.notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
//                    // refresh the recycler
//                  //  TeacherDayRecyclerAdapter.this.dayList.remove(pos);
//                    TeacherDayRecyclerAdapter.this.notifyDataSetChanged();
//
//                    Notification notifiTeacher = new Notification(teacherID, "Lessone signed up",
//                            name + " have singed up alesson at: " + abc.getSingleLesson().getStart() + " on: " + date.toString("dd.MM.yy"));
//                    FirebaseDatabase.getInstance().getReference("Notifications").push().child("notification").setValue(notifiTeacher);
//
//                    // v.setEnabled(false);
//
//                } // end if  free
//
//                else {//if not free
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    final AlertDialog alertDialog1 = builder.show();
//
//                    builder.setMessage("This lesson is taken , please choose another lesson").setTitle("lesson is Taken")
//                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        alertDialog1.dismiss();
//                                    }
//                                }) ;
//                    builder.show() ;
//
//                }// end if taken
//            }//end is Student
//        }//onClick

        boolean didTheStudent = false;
        @Override
        public boolean onLongClick(View v) {
            return false;
        }

        @Override
        public void onClick(View v) {

            TeacherDayRecyclerAdapter.this.pos = this.getAdapterPosition() ;

            if( ref.getString(MainActivity.USER_STUDENT_TEACHER , "").equals("Teacher") )
                initDialog();

            else
                studentPickLesson(dayList.get(getAdapterPosition()));
        }

    }




}
