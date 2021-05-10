import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocialNetwork {
    Map<String, SocialNetworkUser> network = new HashMap<>();

    public SocialNetwork() {};

    public void register(String name) {
        if (!network.containsKey(name)) {
            network.put(name, new SocialNetworkUser());
        }
    }

    public boolean isUserRegistered(String name) {
        return network.containsKey(name);
    }

    public List<String> addFriends(String name, Iterable<String> friends) {
        register(name);

        SocialNetworkUser user = network.get(name);
        List<String> friendList = user.getFriends();
        List<String> newFriends = new ArrayList<>();
        for (String friend : friends) {
            if (friend.equals(name)) {
                System.err.printf("User '%s' tried to befriend himself.\n", name);
                continue;
            }

            if (network.containsKey(friend)) {
                if (!user.isFriend(friend)) {
                    user.addFriend(friend);
                    network.get(friend).addFriend(friend);

                    newFriends.add(friend);
                }
            } else {
                System.err.printf("Could not add friend; '%s' is not a registered user!\n", friend);
            }
        }

        network.put(name, user);

        return newFriends;
    }

    public void sendMessage(String name, String message) {
        SocialNetworkUser user = network.get(name);
        List<String> friendList = user.getFriends();


        for (String friendName : friendList) {
            SocialNetworkUser friend = network.get(friendName);

            friend.addMessage(name + " said: " + message);

            network.put(friendName, friend);
        }
    }

    public List<String> readMessage(String name) {
        SocialNetworkUser user = network.get(name);

        List<String> messages = new ArrayList<>();

        String message = null;
        while ((message = user.readMessage()) != null) {
            messages.add(message);
        }

        if (messages.size() == 0) {
            return null;
        }

        return messages;
    }
}
