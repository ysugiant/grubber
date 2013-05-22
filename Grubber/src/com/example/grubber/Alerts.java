package com.example.grubber;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


import android.app.DialogFragment;
import android.app.FragmentManager;


public class Alerts {

	
	public Alerts() {
	}

    
    public void showNeedServicesDialog() {
	
    }
    
	public static class NeedServicesDialogFragment extends DialogFragment {
	    static NeedServicesDialogFragment newInstance() {
	        return new NeedServicesDialogFragment();
	    }
	
		
		@Override	    
		    public Dialog onCreateDialog(Bundle savedInstanceState) {
		        // Use the Builder class for convenient dialog construction
		        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		        builder.setMessage(R.string.need_services)
		               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   dialog.dismiss();
		                   }
		               });
	
		        // Create the AlertDialog object and return it
		        return builder.create();
		    }
	}
}
    
	
	
	

