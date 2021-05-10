import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SocialNetworkUser {
    List<String> friends = new ArrayList<>();
    Queue<String> messages = new LinkedList<>();

    public List<String> getFriends() {
        return friends;
    }

    public void addFriend(String friend) {
        friends.add(friend);
    }

    public boolean isFriend(String friend) {
        return friends.contains(friend);
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public String readMessage() {
        if (messages.size() > 0) {
            return messages.remove();
        }
        return null;
    }
}
