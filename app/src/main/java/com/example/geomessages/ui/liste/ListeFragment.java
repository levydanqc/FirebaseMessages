package com.example.geomessages.ui.liste;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geomessages.R;
import com.example.geomessages.data.MessagesRoomDatabase;
import com.example.geomessages.databinding.FragmentListeBinding;
import com.example.geomessages.model.Message;
import com.example.geomessages.ui.MessageAdapter;

import java.util.ArrayList;

public class ListeFragment extends Fragment {

    private FragmentListeBinding binding;
    private RecyclerView rvMessages;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messages;
    private MessagesRoomDatabase mDb;
    private ListeViewModel listeViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listeViewModel = new ViewModelProvider(requireActivity()).get(ListeViewModel.class);

        binding = FragmentListeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDb = MessagesRoomDatabase.getDatabase(getContext());

        rvMessages = view.findViewById(R.id.rv_messages);
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages);
        rvMessages.setLayoutManager(new LinearLayoutManager(requireActivity()));
        rvMessages.setAdapter(messageAdapter);

        listeViewModel.getMessages().observe(getViewLifecycleOwner(),
                messagesList -> {
                    messageAdapter.setMessages(messagesList);
                    messageAdapter.notifyDataSetChanged();
                });

        messageAdapter.setOnClickListener(position -> {
            Message clicked = messageAdapter.getMessage(position);
            NavDirections action = ListeFragmentDirections.actionNavListeToNavMaps(clicked.getLatitude(), clicked.getLongitude());
            Navigation.findNavController(view).navigate(action);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}