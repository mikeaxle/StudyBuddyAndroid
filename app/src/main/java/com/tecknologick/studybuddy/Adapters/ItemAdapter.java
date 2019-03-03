package com.tecknologick.studybuddy.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tecknologick.studybuddy.R;
import com.tecknologick.studybuddy.RealmClasses.Module;
import com.tecknologick.studybuddy.RealmClasses.Paper;

import io.realm.RealmList;

public class ItemAdapter extends RecyclerView.Adapter {
    // string to identify the item type of the list
    private String type;
    private RealmList<?> dataSet;
    Object additionalInfo;

    // item click listener
    public com.tecknologick.studybuddy.Adapters.ItemAdapter.OnItemClickListener listener;

    // constructor
    public ItemAdapter(String _type, RealmList<?> _dataSet) {
        type = _type;
        dataSet = _dataSet;
    }

    // constructor with additional info object
    public ItemAdapter(String _type, RealmList<?> _dataSet, Object _additionalInfo) {
        type = _type;
        dataSet = _dataSet;
        additionalInfo = _additionalInfo;
    }

    //**** View holder classes ****//
    class ModuleHolder extends RecyclerView.ViewHolder {
        // declare objects to hold views
        ImageView image;
        TextView moduleName;
        TextView paperCount;

        // constructor
        public ModuleHolder(View itemView) {
            super(itemView);

            // get views
            image = itemView.findViewById(R.id.moduleImageButton);
            moduleName = itemView.findViewById(R.id.moduleRowLabel);
            paperCount = itemView.findViewById(R.id.moduleRowPaperCount);

            // set click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get position
                    int position = getAdapterPosition();

                    // check if position and listener is null
                    if (position != RecyclerView.NO_POSITION && listener != null) {

                        // get item
                        Object module = dataSet.get(position);

                        // return item with position
                        listener.onItemClick(module, position);
                    }
                }
            });
        }
    }

    class PaperViewHolder extends RecyclerView.ViewHolder {
        // declare objects to hold views
        TextView paperName;
        TextView paperDate;
        ImageView image;

        public PaperViewHolder(View itemView) {
            super(itemView);

            // get views
            paperName = itemView.findViewById(R.id.paperRowLabel);
            paperDate = itemView.findViewById(R.id.paperRowDate);
            image = itemView.findViewById(R.id.paperRowImage);

            // set click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get position
                    int position = getAdapterPosition();

                    // check if position and listener is null
                    if (position != RecyclerView.NO_POSITION && listener != null) {

                        // get item
                        Object paper = dataSet.get(position);

                        // return item with position
                        listener.onItemClick(paper, position);
                    }
                }
            });
        }
    }


    //**** Create Views ****//
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // create view to hold item
        View view = null;

        // check type of item
        switch (type) {
            case "modules":
                // inflate module list layout
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.row_module_list, parent, false);
                // return view
                return new ModuleHolder(view);

            case "papers":
                // inflate paper list layout
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.row_paper_list, parent, false);
                // return view
                return new PaperViewHolder(view);
            default:
                break;
        }

        return null;
    }

    //**** Replace Content on Views ****//
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (type) {
            case "modules":
                // cast model to module
                Module module = (Module) dataSet.get(position);

                // set views
                ((ModuleHolder) viewHolder).moduleName.setText(module.name);
                ((ModuleHolder) viewHolder).paperCount.setText(module.papers.size() + " papers");
                break;
            case "papers":
                // cast model to paper
                Paper paper = (Paper) dataSet.get(position);

                // set views
                ((PaperViewHolder) viewHolder).paperName.setText(paper.name);
                ((PaperViewHolder) viewHolder).paperDate.setText(paper.year);
                break;
            default:
                break;

        }

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Object item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
