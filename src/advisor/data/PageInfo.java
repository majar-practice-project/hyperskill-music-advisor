package advisor.data;

import com.google.gson.JsonElement;

import java.util.List;

public class PageInfo {
    private int currentPage;
    private int totalPages;
    private List<JsonElement> els;

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<JsonElement> getEls() {
        return els;
    }

    public PageInfo(int currentPage, int totalPages, List<JsonElement> els) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.els = els;
    }
}
