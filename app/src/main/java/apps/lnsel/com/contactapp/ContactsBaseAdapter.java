package apps.lnsel.com.contactapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import apps.lnsel.com.contactapp.VolleyLibrary.AppController;

/**
 * Created by apps2 on 7/17/2017.
 */
public class ContactsBaseAdapter extends BaseAdapter {


    Context context;
    private static LayoutInflater inflater=null;

    private List<ContactsSetterGetter> contactsList = null;
    private ArrayList<ContactsSetterGetter> arraylist;

    public ContactsBaseAdapter(Activity context, List<ContactsSetterGetter> contactsList) {
        this.context = context;

        this.contactsList = contactsList;
        this.arraylist = new ArrayList<ContactsSetterGetter>();
        this.arraylist.addAll(contactsList);

        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public View getView(final int position, final View view, ViewGroup parent) {
        View rowView=inflater.inflate(R.layout.cardview_contacts, null,true);

        TextView tv_contact_person_name = (TextView) rowView.findViewById(R.id.cardview_contacts_tv_person_name);
        TextView tv_contact_no = (TextView) rowView.findViewById(R.id.cardview_contacts_tv_contact_no);
        TextView tv_contact_address = (TextView) rowView.findViewById(R.id.cardview_contacts_tv_address);
        TextView tv_contact_email = (TextView) rowView.findViewById(R.id.cardview_contacts_tv_email);
        final ImageView iv_contact_image = (ImageView) rowView.findViewById(R.id.cardview_contacts_iv_image);

        ImageButton ib_call_contact = (ImageButton) rowView.findViewById(R.id.cardview_contacts_ib_call_contact);
        ImageButton ib_sms = (ImageButton) rowView.findViewById(R.id.cardview_contacts_ib_sms);
        ImageButton ib_direction = (ImageButton) rowView.findViewById(R.id.cardview_contacts_ib_direction);
        ImageButton ib_share = (ImageButton) rowView.findViewById(R.id.cardview_contacts_ib_share);
        ImageButton ib_edit_contact = (ImageButton) rowView.findViewById(R.id.cardview_contacts_ib_edit_contact);
        ImageButton ib_delete_contact = (ImageButton) rowView.findViewById(R.id.cardview_contacts_ib_delete_contact);

        tv_contact_person_name.setText(contactsList.get(position).getCntPersonName());
        tv_contact_no.setText(contactsList.get(position).getCntContactNo());
        tv_contact_address.setText(contactsList.get(position).getCntAddress());
        tv_contact_email.setText(contactsList.get(position).getCntEmail());

        if(contactsList.get(position).getCntImageStatus().equals("true")){

            ImageLoader imageLoader = AppController.getInstance().getImageLoader();
            imageLoader.get(contactsList.get(position).getCntImage(), new ImageLoader.ImageListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Image Load Error: ",error.getMessage());
                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                    if (response.getBitmap() != null) {
                        iv_contact_image.setImageBitmap(response.getBitmap());
                    }
                }
            });

        }

        ib_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Name: "+contactsList.get(position).getCntPersonName().toString()+"\n"+
                                    "Contact No.: "+contactsList.get(position).getCntContactNo().toString();
                ((MainActivity)context).shareContact(message);
            }
        });

        ib_call_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).callingMethod(contactsList.get(position).getCntContactNo());
            }
        });

        ib_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).startGoogleMapDirectionActivity(contactsList.get(position).getCntAddress());
            }
        });

        ib_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).smsmethod(contactsList.get(position).getCntContactNo());
            }
        });

        ib_edit_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContactsData.current_cntId = contactsList.get(position).getCntId();
                ContactsData.current_cntPersonName = contactsList.get(position).getCntPersonName();
                ContactsData.current_cntNumber = contactsList.get(position).getCntContactNo();
                ContactsData.current_cntEmail = contactsList.get(position).getCntEmail();
                ContactsData.current_cntAddress = contactsList.get(position).getCntAddress();
                ContactsData.current_cntImageStatus = contactsList.get(position).getCntImageStatus();
                ContactsData.current_cntImage = contactsList.get(position).getCntImage();

                Intent intent = new Intent((Activity) context, EditContactActivity.class);
                ((Activity) context).startActivity(intent);

            }
        });

        ib_delete_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).deleteContactWebService(contactsList.get(position).getCntId());
            }
        });


        return rowView;

    }


    // Filter Class
    public void filter(String charText, View btn_clear) {
        charText = charText.toLowerCase(Locale.getDefault());
        contactsList.clear();
        if (charText.length() == 0||charText.equalsIgnoreCase("")) {
            contactsList.addAll(arraylist);
            btn_clear.setVisibility(View.GONE);
        }
        else
        {
            for (ContactsSetterGetter wp : arraylist)
            {
                if (wp.getCntPersonName().toLowerCase(Locale.getDefault()).contains(charText)||
                        wp.getCntContactNo().toLowerCase(Locale.getDefault()).contains(charText)||
                        wp.getCntAddress().toLowerCase(Locale.getDefault()).contains(charText)||
                        wp.getCntEmail().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    contactsList.add(wp);
                }
            }
            btn_clear.setVisibility(View.VISIBLE);
        }
        notifyDataSetChanged();
    }





    @Override
    public int getCount() {
        return contactsList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
