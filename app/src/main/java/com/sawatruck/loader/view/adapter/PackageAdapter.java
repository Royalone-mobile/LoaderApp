package com.sawatruck.loader.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.BulkType;
import com.sawatruck.loader.entities.MeasureUnit;
import com.sawatruck.loader.entities.Package;
import com.sawatruck.loader.entities.TruckBrand;
import com.sawatruck.loader.entities.TruckType;
import com.sawatruck.loader.utils.Notice;
import com.sawatruck.loader.view.design.CustomEditText;
import com.sawatruck.loader.view.design.CustomTextView;
import com.sawatruck.loader.view.design.NumberButton;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/22/2017.
 */
public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.MyViewHolder> {

    private ArrayList<Package> packages = new ArrayList<>();
    private Context context;

    public static ArrayList<TruckType> truckTypes = new ArrayList<>();
    public static ArrayList<TruckBrand> truckBrands = new ArrayList<>();
    public static ArrayList<MeasureUnit> measureUnits = new ArrayList<>();
    public static ArrayList<BulkType> bulkTypes = new ArrayList<>();


    public static int loadTypeID = -1;

    public static int getLoadTypeID() {
        return loadTypeID;
    }

    public static void setLoadTypeID(int loadTypeID) {
        PackageAdapter.loadTypeID = loadTypeID;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.package_list_item, parent, false);
        return new MyViewHolder(v);
    }

    public PackageAdapter() {

    }

    public PackageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if(loadTypeID == 3) {
            holder.spinnerVehicleType.setHint(context.getString(R.string.bulk_type));
            holder.spinnerVehicleBrand.setVisibility(View.GONE);
            holder.txtQuantity.setVisibility(View.GONE);
            holder.txtModel.setHint(context.getString(R.string.volume));

        }
        else {
            holder.spinnerVehicleType.setHint(context.getString(R.string.vehicle_type));
            holder.spinnerVehicleBrand.setVisibility(View.VISIBLE);
            holder.txtQuantity.setVisibility(View.VISIBLE);
            holder.txtModel.setHint(context.getString(R.string.model));
        }


        ArrayList<String> strTruckTypes = new ArrayList<>();
        for(int i = 0; i <truckTypes.size(); i++){
            strTruckTypes.add(truckTypes.get(i).getName());
        }

        ArrayAdapter truckTypeAdapter = new ArrayAdapter(
                context,
                R.layout.spinner_item,
                strTruckTypes);
        truckTypeAdapter.setDropDownViewResource(R.layout.spinner_select);
        holder.spinnerVehicleType.setAdapter(truckTypeAdapter);
        if(truckTypes.size()>0)
            holder.spinnerVehicleType.setSelection(0);

        ArrayList<String> strTruckBrands = new ArrayList<>();
        for(int i = 0; i <truckBrands.size(); i++){
            strTruckBrands.add(truckBrands.get(i).getName());
        }

        ArrayAdapter truckBrandAdapter = new ArrayAdapter(
                context,
                R.layout.spinner_item,
                strTruckBrands);
        truckBrandAdapter.setDropDownViewResource(R.layout.spinner_select);
        holder.spinnerVehicleBrand.setAdapter(truckBrandAdapter);

        ArrayList<String> strMeasureUnits = new ArrayList<>();

        if(loadTypeID == 6)
            for(int i = 0; i <measureUnits.size(); i++){
                strMeasureUnits.add(measureUnits.get(i).getName());
            }
        else
            for(int i = 0; i <bulkTypes.size(); i++){
                strMeasureUnits.add(bulkTypes.get(i).getName());
            }

        ArrayAdapter measureUnitsAdapter = new ArrayAdapter(
                context,
                R.layout.spinner_item,
                strMeasureUnits);
        measureUnitsAdapter.setDropDownViewResource(R.layout.spinner_select);
        holder.spinnerMeasureUnit.setAdapter(measureUnitsAdapter);


        holder.txtPackageName.setText(context.getString(R.string.package_name) + " " + String.valueOf(position+1));

        final Package item = packages.get(position);

        holder.spinnerVehicleType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setTruckType(truckTypes.get(position));
                packages.set(position,item);
            }
        });

        holder.txtQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                item.setQuantity(s.toString());
                packages.set(position,item);
            }
        });

        holder.txtModel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                item.setModel(s.toString());
                packages.set(position,item);
            }
        });

        holder.spinnerVehicleBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setTruckBrand(truckBrands.get(position));
                packages.set(position,item);
            }
        });

        holder.spinnerMeasureUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loadTypeID ==6)
                    item.setMeasureUnit(measureUnits.get(position));
                else
                    item.setBulkType(bulkTypes.get(position));

                packages.set(position,item);
            }
        });

        holder.numberUnitWeight.textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                item.setUnitWeight(Long.valueOf(holder.numberUnitWeight.getNumber()));
                packages.set(position,item);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    public ArrayList<Package> getPackages() {
        return packages;
    }

    public void setPackages(ArrayList<Package> packages) {
        this.packages = packages;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_quantity) CustomEditText txtQuantity;
        @Bind(R.id.spinner_vehicle_type) BetterSpinner spinnerVehicleType;
        @Bind(R.id.spinner_vehicle_brand) BetterSpinner spinnerVehicleBrand;
        @Bind(R.id.number_unit_weight) NumberButton numberUnitWeight;
        @Bind(R.id.spinner_measure_unit) BetterSpinner spinnerMeasureUnit;
        @Bind(R.id.txt_package_name) CustomTextView txtPackageName;
        @Bind(R.id.txt_model) CustomEditText txtModel;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

