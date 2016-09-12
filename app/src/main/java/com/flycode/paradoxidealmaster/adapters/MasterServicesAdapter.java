package com.flycode.paradoxidealmaster.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.flycode.paradoxidealmaster.adapters.viewholders.OnItemClickListener;
import com.flycode.paradoxidealmaster.adapters.viewholders.ServicesMasterViewHolder;
import com.flycode.paradoxidealmaster.adapters.viewholders.ServicesViewHolder;
import com.flycode.paradoxidealmaster.adapters.viewholders.SuperViewHolder;
import com.flycode.paradoxidealmaster.model.IdealMasterService;
import com.flycode.paradoxidealmaster.model.IdealService;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by acerkinght on 8/30/16.
 */
public class MasterServicesAdapter extends RecyclerView.Adapter<SuperViewHolder> implements OnItemClickListener, ServicesViewHolder.ServiceProvider, ServicesMasterViewHolder.MasterServiceProvider {
    private static final int TYPE_SERVICE = 0;
    private static final int TYPE_DETAILED_SERVICE = 2;

    private Context context;
    private ArrayList<IdealService> rootServices;
    private ArrayList<IdealMasterService> currentServices;
    private HashMap<String, ArrayList<IdealService>> subServices;
    private HashMap<String, ArrayList<IdealMasterService>> finalServices;
    private String currentExpendedServiceId;
    private int currentExpendedServiceIndex;

    public MasterServicesAdapter(Context context) {
        this.context = context;

        rootServices = new ArrayList<>();
        currentServices = new ArrayList<>();
        subServices = new HashMap<>();
        finalServices = new HashMap<>();
    }

    @Override
    public SuperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SERVICE) {
            return ServicesViewHolder.initialize(context, parent, this, this);
        } else if (viewType == TYPE_DETAILED_SERVICE) {
            return ServicesMasterViewHolder.initialize(context, parent, this, this);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(SuperViewHolder holder, int position) {
        holder.setupForPosition(position);
    }

    @Override
    public int getItemViewType(int position) {
        Object service = getRealServiceForPosition(position);

        if (service instanceof IdealMasterService) {
            return TYPE_DETAILED_SERVICE;
        }

        return TYPE_SERVICE;
    }

    @Override
    public int getItemCount() {
        if (rootServices.isEmpty()
                || finalServices.isEmpty()) {
            return 0;
        }

        if (currentExpendedServiceId != null
            && !currentExpendedServiceId.isEmpty()) {
            return rootServices.size() + currentServices.size();
        }

        return rootServices.size();
    }

    @Override
    public void onItemClicked(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ServicesViewHolder) {
            IdealService service = (IdealService) getRealServiceForPosition(position);

            if (currentExpendedServiceId != null) {
                String savedExpendedServiceId = currentExpendedServiceId;
                currentExpendedServiceId = null;
                notifyItemRangeRemoved(currentExpendedServiceIndex, currentServices.size());

                if (savedExpendedServiceId.equals(service.getId())) {
                    return;
                }
            }

            currentExpendedServiceId = service.getId();
            currentServices.clear();

            for (int index = 0 ; index < rootServices.size() ; index++) {
                if (rootServices.get(index).getId().equals(currentExpendedServiceId)) {
                    currentExpendedServiceIndex = index + 1;

                    for (IdealService subService : subServices.get(service.getId())) {
                        if (finalServices.containsKey(subService.getId())) {
                            currentServices.addAll(finalServices.get(subService.getId()));
                        }
                    }

                    if (currentServices.isEmpty()) {
                        currentExpendedServiceId = null;
                        return;
                    }

                    notifyItemRangeInserted(position + 1, currentServices.size());
                    return;
                }
            }
        }
    }

    @Override
    public IdealService getService(int position) {
        return (IdealService) getRealServiceForPosition(position);
    }

    @Override
    public boolean isServiceExpended(int position) {
        return ((IdealService) getRealServiceForPosition(position)).getId().equals(currentExpendedServiceId);
    }

    @Override
    public IdealMasterService getMasterServiceForPosition(int position) {
        return currentServices.get(position - currentExpendedServiceIndex);
    }

    private Object getRealServiceForPosition(int position) {
        if (currentExpendedServiceId != null
                && !currentExpendedServiceId.isEmpty()) {
            if (position >= currentExpendedServiceIndex) {
                if (position >= currentServices.size() + currentExpendedServiceIndex) {
                    position = position - currentServices.size();
                    return rootServices.get(position);
                } else {
                    return currentServices.get(position - currentExpendedServiceIndex);
                }
            } else {
                return rootServices.get(position);
            }
        } else {
            return rootServices.get(position);
        }
    }

    public void setServices(ArrayList<IdealService> idealServices) {
        rootServices.clear();
        subServices.clear();

        for (IdealService service : idealServices) {
            if (service.getSuperService() == null
                    || service.getSuperService().isEmpty()) {
                rootServices.add(service);
            } else if (!service.isFinal()) {
                if (!subServices.containsKey(service.getSuperService())) {
                    subServices.put(service.getSuperService(), new ArrayList<IdealService>());
                }

                subServices.get(service.getSuperService()).add(service);
            }
        }

        notifyDataSetChanged();
    }

    public void setMasterServices(ArrayList<IdealMasterService> masterServices) {
        finalServices.clear();

        for (IdealMasterService service : masterServices) {
            if (service.isFinal()) {
                if (!finalServices.containsKey(service.getSuperService())) {
                    finalServices.put(service.getSuperService(), new ArrayList<IdealMasterService>());
                }

                finalServices.get(service.getSuperService()).add(service);
            }
        }

        notifyDataSetChanged();
    }
}