package prog12;

import java.util.*;

public class Tinge implements SearchEngine {
	
	HardDisk<PageFile> pageFiles = new HardDisk<PageFile>();
	PageTrie url2index = new PageTrie();
	HardDisk<List<Long>> wordFiles = new HardDisk<>();
	WordTable word2index = new WordTable();
	
	Long indexPage(String url) {
		Long index = pageFiles.newFile();
		PageFile pageFile = new PageFile(index, url);
		System.out.println("indexing page " + pageFile);
		pageFiles.put(index, pageFile);
		url2index.put(url, index);
		return index;
	}
	
	Long indexWord(String word) {
		Long index = wordFiles.newFile();
		List<Long> wordFile = new ArrayList<Long>();
		System.out.println("indexing word " + index + "(" + word + ")" + wordFile);
		wordFiles.put(index, wordFile);
		word2index.put(word, index);
		return index;
	}
	
	/* Gather info from all web pages reachable from URLs in startingURLs. */
    public void gather (Browser browser, List<String> startingURLs) {
    		Queue<Long> pageQueue = new ArrayDeque<Long>(); // queue of pageIndices
    		
    		for(String url : startingURLs) {
    			if(!url2index.containsKey(url)) {
    				Long tempPage = indexPage(url);
				pageQueue.offer(tempPage);
    			}
    		}
    		
    		while(!pageQueue.isEmpty()) {
    			System.out.println("queue " + pageQueue);
    			Long pageIndex = pageQueue.poll();
    			PageFile pageFile = pageFiles.get(pageIndex);
    			System.out.println("dequeue " + pageFile);
    			
    			if(browser.loadPage(pageFile.url)) {
    				List<String> urls = browser.getURLs();
    				System.out.println("urls " + urls);
    				Set<Long> pageIndices = new HashSet<Long>();
    				for(String url : urls) {
    					if(!url2index.containsKey(url)) {
    						Long tempPage = indexPage(url);
    		    				pageQueue.offer(tempPage);
    		    				pageIndices.add(tempPage);
    					} else
    						pageIndices.add(url2index.get(url));
    		    		}
    				
    				for(Long linkedIndex : pageIndices) {
    					pageFiles.get(linkedIndex).incRefCount();
    					System.out.println("inc ref " + pageFiles.get(linkedIndex));
    				}
    				
    				List<String> words = browser.getWords();
    				System.out.println("words" + words);
    				for(String word : words) {
    					if(!word2index.containsKey(word))
    						indexWord(word);
    					Long index = word2index.get(word);
    					List<Long> wordFile = wordFiles.get(index);
    					if(wordFile.isEmpty() || pageIndex != wordFile.get(wordFile.size()-1)) {
	    					wordFile.add(pageIndex);	
	    					System.out.println("add page " + index + "(" + word + ")" + wordFile);
    					}
	    			}
    			}
    		}
    		
    		System.out.println("pageFiles\n" + pageFiles);
    		System.out.println("url2index\n" + url2index);
    		System.out.println("wordFiles\n" + wordFiles);
    		System.out.println("word2index\n" + word2index);
    		
    		url2index.write(pageFiles);
    		word2index.write(wordFiles);
    		
    }
    
    public String[] search (List<String> keyWords, int numResults) {
    		return new String[0];
    }

}
