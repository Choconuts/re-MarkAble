package com.example.choconut.re_markable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;

public class MarkTable implements Serializable {
    private static final long serialVersionUID = 1L;
    enum MarkType {
        ENTITY, RELATION, NONE
    }
    private MarkType markType;
    private boolean uniqueIdCheck = false;
    private DocumentEntity entityDoc;
    private DocumentRelation tripleDoc;
    private LinkedList<Group> article;
    private LinkedList<Group> Generate(LinkedList<WordToken> list) {
        LinkedList<Group> res = new LinkedList<>();
        for(WordToken wordToken:list){
            res.add(new Group(wordToken));
        }
        return res;
    }
    private Group combine(Group groupStart, Group groupEnd) {
        if(groupEnd.wordTokens.getFirst().start < groupStart.wordTokens.getFirst().start) return combine(groupEnd, groupStart);
        if(groupStart == groupEnd) {
            return groupStart;
        }
        int fromIndex = article.indexOf(groupStart);
        int toIndex = article.indexOf(groupEnd);
        LinkedList<Group> sublist = (LinkedList<Group>) article.subList(fromIndex, toIndex);
        Group combinedGroup = new Group();
        for (Group g: sublist) {
            if (markType == MarkType.ENTITY)
                g.clearEntity();
            else if (markType == MarkType.RELATION)
                g.clearRelation();
            combinedGroup.wordTokens.add(g.wordTokens.getFirst());
        }
        article.removeAll(sublist);
        article.add(fromIndex, combinedGroup);
        return combinedGroup;
    }
    void decompose(Group group) {
        if (group.wordTokens.size() <= 1) return;
        int index = article.indexOf(group);
        LinkedList<Group> newList = Generate(group.wordTokens);
        article.remove(group);
        article.addAll(index, newList);
    }
    private boolean bindGroup(Triple triple) {
        MarkTable.Group s = findByStart(triple.left_start);
        MarkTable.Group e = findByEnd(triple.left_end);
        if (s == null || e == null) return false;
        triple.leftGroupId = combine(s, e).id;
        s = findByStart(triple.right_start);
        e = findByEnd(triple.right_end);
        if (s == null || e == null) return false;
        triple.rightGroupId = combine(s, e).id;
        if (triple.relation_start != -1 && triple.relation_end != -1){
            s = findByStart(triple.relation_start);
            e = findByEnd(triple.relation_end);
            if (s == null || e == null) return false;
            triple.relationGroupId = combine(s, e).id;
        }
        return true;
    }
    private Group findByStart(int start) {
        for (Group g: article) {
            if (g.wordTokens.getFirst().start <= start && g.wordTokens.getLast().end > start) return g;
        }
        return null;
    }
    private Group findByEnd(int end) {
        for (Group g: article) {
            if (g.wordTokens.getLast().end >= end && g.wordTokens.getFirst().start < end) return g;
        }
        return null;
    }
    private Group findById(String id) {
        for (Group group: article) {
            if (group.id.equals(id)) {
                return group;
            }
        }
        return null;
    }

    //group 就是一个可以按的语句块，比如"唐纳德·特朗普"
    class Group implements Serializable {
        private static final long serialVersionUID = 1L;

        String id;
        private ArrayList<String> occupate = new ArrayList<>();
        LinkedList<WordToken> wordTokens = new LinkedList<>();
        private Group(WordToken wordToken) {
            wordTokens.add(wordToken);
        }
        private Group(){
            id = Document.genId(24);
        }
        String getEntityName(){
            StringBuilder name = new StringBuilder();
            for (WordToken word:wordTokens){
                name.append(word.word);
            }
            return name.toString();
        }
        private void clearEntity(){
            if(occupate.size() == 0) return;
            entityDoc.erase(occupate.get(0));
            occupate.clear();
        }
        private void clearRelation(){
            LinkedList<String> temp = new LinkedList<>();
            temp.addAll(occupate);
            for (String id: temp){
                for(Group g: article){
                    g.removeRelation(id);
                }
                tripleDoc.erase(id);
            }
        }
        private void removeRelation(String removeId){
            for (String id: occupate){
                if (id.equals(removeId)){
                    occupate.remove(id);
                    break;
                }
            }
            if (occupate.size() == 0) decompose(this);
        }
    }

    //generation
    static MarkTable load(String filename) {
        FileHelper fileHelper = new FileHelper();
        MarkTable res = (MarkTable) fileHelper.read(filename);
        System.out.println("res == null: " + (res==null));
        return res;
    }
    MarkTable(MarkType type, JSONObject info, JSONObject txData) {
        markType = type;
        if (markType == MarkType.ENTITY) {
            entityDoc = new DocumentEntity(info);
            article = Generate(WordToken.Generate(txData));
        }
        if (markType == MarkType.RELATION) {
            article = Generate(WordToken.Generate(txData));
            tripleDoc = new DocumentRelation(info);
            for (Triple triple: tripleDoc.triples) {
                if (!bindGroup(triple)) {
                    System.out.println("Broken Triple");
                }
            }
        }
    }
    public void setUniqueIdCheck(boolean uniqueIdCheck) {
        this.uniqueIdCheck = uniqueIdCheck;
    }

    //record
    boolean save(String filename) {
        FileHelper fileHelper = new FileHelper();
        fileHelper.save(filename, this);
        return true;
    }
    JSONObject publish() {
        if (markType == MarkType.ENTITY) {
            return entityDoc.toJson();
        }
        else if (markType == MarkType.RELATION) {
            return tripleDoc.toJson();
        }
        return null;
    }

    //query
    public LinkedList<Group> getArticleGoups() {
        return article;
    }
    public Document getDocument() {
        if (markType == MarkType.ENTITY)
            return entityDoc;
        if (markType == MarkType.RELATION)
            return tripleDoc;
        return null;
    }
    public DocumentEntity getEntityDocument() {
        if (markType == MarkType.RELATION) return null;
        return entityDoc;
    }
    public DocumentRelation getTripleDocument() {
        if (markType == MarkType.ENTITY) return null;
        return tripleDoc;
    }
    public MarkType getMarkType() {
        return markType;
    }
    LinkedList<Entity> getOccupateEntities(Group groupStart, Group groupEnd) {
        if(groupEnd.wordTokens.getFirst().start < groupStart.wordTokens.getFirst().start) return getOccupateEntities(groupEnd, groupStart);
        if(groupStart == groupEnd) {
            return getEntitiesByGroup(groupStart);
        }
        int fromIndex = article.indexOf(groupStart);
        int toIndex = article.indexOf(groupEnd);
        LinkedList<Group> sublist = (LinkedList<Group>) article.subList(fromIndex, toIndex);
        LinkedList<Entity> list = new LinkedList<>();
        for (Group g: sublist) {
            list.addAll(getEntitiesByGroup(g));
        }
        return list;
    }
    LinkedList<Triple> getOccupateTriples(Group groupStart, Group groupEnd) {
        if(groupEnd.wordTokens.getFirst().start < groupStart.wordTokens.getFirst().start) return getOccupateTriples(groupEnd, groupStart);
        if(groupStart == groupEnd) {
            return getTriplesByGroup(groupStart, SearchMode.ALL);
        }
        int fromIndex = article.indexOf(groupStart);
        int toIndex = article.indexOf(groupEnd);
        LinkedList<Group> sublist = (LinkedList<Group>) article.subList(fromIndex, toIndex);
        LinkedList<Triple> list = new LinkedList<>();
        for (Group g: sublist) {
            list.addAll(getTriplesByGroup(g, SearchMode.ALL));
        }
        return list;
    }

    //user operation (添加操作会先删除)
    boolean addEntity(Group group, String nerTag) {
        if(markType != MarkType.ENTITY) return false;
        Entity entity = new Entity(group, nerTag);
        if (deleteEntity(group)) {
            System.out.print("覆盖了旧的实体！");
        }
        entityDoc.insert(entity);
        group.occupate.add(entity.id);
        return true;
    }
    boolean deleteEntity(Group group) {
        if(markType != MarkType.ENTITY) return false;
        try {
            group.clearEntity();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    boolean addTriple(Group groupLeft, Group groupRight, Group groupRelation, int relationId) {
        if(groupLeft == null || groupRight == null) return false;
        Triple triple = new Triple(groupLeft, groupRight, groupRelation, relationId);

        if (deleteTriple(groupLeft, groupRight)) {
            System.out.print("覆盖了旧的三元组！");
        }
        tripleDoc.insert(triple);
        groupLeft.occupate.add(triple.id);
        groupRight.occupate.add(triple.id);
        if (groupRelation != null) {
            groupRelation.occupate.add(triple.id);
        }
        return true;
    }
    boolean deleteTriple(Group groupLeft, Group groupRight) {
        boolean flag = false;
        for (String idLeft: groupLeft.occupate){
            for (String idRight: groupRight.occupate){
                if (idLeft.equals(idRight)) {
                    for (Group group: article) {
                        group.removeRelation(idLeft);
                    }
                    tripleDoc.erase(idLeft);
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    //helper
    enum SearchMode {
        LEFT, RIGHT, MIDDLE, ALL, LEFTANDRIGHT
    }
    LinkedList<WordToken> getWords(){
        LinkedList<WordToken> list = new LinkedList<>();
        for (Group g: article) {
            list.addAll(g.wordTokens);
        }
        return  list;
    }
    //if group is null, it will return all
    LinkedList<Triple> getTriples() {
        LinkedList<Triple> list = new LinkedList<>();
        list.addAll(tripleDoc.triples);
        return list;
    }
    LinkedList<Entity> getEntities() {
        LinkedList<Entity> list = new LinkedList<>();
        list.addAll(entityDoc.entities);
        return list;
    }
    LinkedList<Triple> getTriplesByGroup(Group group, SearchMode searchMode){
        LinkedList<Triple> list = new LinkedList<>();
        if (group == null){
            list.addAll(tripleDoc.triples);
            return list;
        }
        for (String id: group.occupate) {
            for (Triple t: tripleDoc.triples){
                switch (searchMode){
                    case LEFT:
                        if (t.leftGroupId.equals(id))
                            list.add(t);
                    case RIGHT:
                        if (t.rightGroupId.equals(id))
                            list.add(t);
                    case MIDDLE:
                        if (t.relationGroupId.equals(id))
                            list.add(t);
                    case ALL:
                        if (t.leftGroupId.equals(id) | t.rightGroupId.equals(id) | t.relationGroupId.equals(id))
                            list.add(t);
                    case LEFTANDRIGHT:
                        if (t.leftGroupId.equals(id) | t.rightGroupId.equals(id))
                            list.add(t);
                }
            }
        }
        return list;
    }
    LinkedList<Entity> getEntitiesByGroup(Group group){
        LinkedList<Entity> list = new LinkedList<>();
        if (group == null){
            list.addAll(entityDoc.entities);
            return list;
        }
        for (String id: group.occupate) {
            for (Entity e: entityDoc.entities){
                if (e.groupId.equals(id))
                    list.add(e);
            }
        }
        return list;
    }


    //query
    public LinkedList<String> getArticle() {
        LinkedList<String> list = new LinkedList<>();
        for (Group group: article) {
            list.add(group.id);
        }
        return list;
    }
    LinkedList<Entity> getOccupateEntities(String startId, String endId) {
        Group groupStart = findById(startId);
        Group groupEnd = findById(endId);
        if(groupEnd.wordTokens.getFirst().start < groupStart.wordTokens.getFirst().start) return getOccupateEntities(groupEnd, groupStart);
        if(groupStart == groupEnd) {
            return getEntitiesByGroup(groupStart);
        }
        int fromIndex = article.indexOf(groupStart);
        int toIndex = article.indexOf(groupEnd);
        LinkedList<Group> sublist = (LinkedList<Group>) article.subList(fromIndex, toIndex);
        LinkedList<Entity> list = new LinkedList<>();
        for (Group g: sublist) {
            list.addAll(getEntitiesByGroup(g));
        }
        return list;
    }
    LinkedList<Triple> getOccupateTriples(String groupStartId, String groupEndId) {
        Group groupStart = findById(groupStartId);
        Group groupEnd = findById(groupEndId);
        if(groupEnd.wordTokens.getFirst().start < groupStart.wordTokens.getFirst().start) return getOccupateTriples(groupEnd, groupStart);
        if(groupStart == groupEnd) {
            return getTriplesByGroup(groupStart, SearchMode.ALL);
        }
        int fromIndex = article.indexOf(groupStart);
        int toIndex = article.indexOf(groupEnd);
        LinkedList<Group> sublist = (LinkedList<Group>) article.subList(fromIndex, toIndex);
        LinkedList<Triple> list = new LinkedList<>();
        for (Group g: sublist) {
            list.addAll(getTriplesByGroup(g, SearchMode.ALL));
        }
        return list;
    }

    //user operation (添加操作会先删除)
    boolean addEntity(String groupId, String nerTag) {
        Group group = findById(groupId);
        if(markType != MarkType.ENTITY) return false;
        Entity entity = new Entity(group, nerTag);
        if (deleteEntity(group)) {
            System.out.print("覆盖了旧的实体！");
        }
        entityDoc.insert(entity);
        group.occupate.add(entity.id);
        return true;
    }
    boolean deleteEntity(String groupId) {
        Group group = findById(groupId);
        if(markType != MarkType.ENTITY) return false;
        try {
            group.clearEntity();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    boolean addTriple(String groupLeftId, String groupRightId, String groupRelationId, int relationId) {

        Group groupLeft = findById(groupLeftId);
        Group groupRight = findById(groupRightId);
        Group groupRelation = findById(groupRelationId);

        if(groupLeft == null || groupRight == null) return false;
        Triple triple = new Triple(groupLeft, groupRight, groupRelation, relationId);

        if (deleteTriple(groupLeft, groupRight)) {
            System.out.print("覆盖了旧的三元组！");
        }
        tripleDoc.insert(triple);
        groupLeft.occupate.add(triple.id);
        groupRight.occupate.add(triple.id);
        if (groupRelation != null) {
            groupRelation.occupate.add(triple.id);
        }
        return true;
    }
    boolean deleteTriple(String groupLeftId, String groupRightId) {
        Group groupLeft = findById(groupLeftId);
        Group groupRight = findById(groupRightId);
        boolean flag = false;
        for (String idLeft: groupLeft.occupate){
            for (String idRight: groupRight.occupate){
                if (idLeft.equals(idRight)) {
                    for (Group group: article) {
                        group.removeRelation(idLeft);
                    }
                    tripleDoc.erase(idLeft);
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    //helper
    LinkedList<Triple> getTriplesByGroup(String groupId, SearchMode searchMode){
        Group group = findById(groupId);
        LinkedList<Triple> list = new LinkedList<>();
        if (group == null){
            list.addAll(tripleDoc.triples);
            return list;
        }
        for (String id: group.occupate) {
            for (Triple t: tripleDoc.triples){
                switch (searchMode){
                    case LEFT:
                        if (t.leftGroupId.equals(id))
                            list.add(t);
                    case RIGHT:
                        if (t.rightGroupId.equals(id))
                            list.add(t);
                    case MIDDLE:
                        if (t.relationGroupId.equals(id))
                            list.add(t);
                    case ALL:
                        if (t.leftGroupId.equals(id) | t.rightGroupId.equals(id) | t.relationGroupId.equals(id))
                            list.add(t);
                    case LEFTANDRIGHT:
                        if (t.leftGroupId.equals(id) | t.rightGroupId.equals(id))
                            list.add(t);
                }
            }
        }
        return list;
    }
    LinkedList<Entity> getEntitiesByGroup(String groupId){
        Group group = findById(groupId);
        LinkedList<Entity> list = new LinkedList<>();
        if (group == null){
            list.addAll(entityDoc.entities);
            return list;
        }
        for (String id: group.occupate) {
            for (Entity e: entityDoc.entities){
                if (e.groupId.equals(id))
                    list.add(e);
            }
        }
        return list;
    }

}

abstract class Document implements Serializable  {
    private static final long serialVersionUID = 1L;

    String title;
    String content;     //also called sent_ctx
    String doc_id;
    String sent_id;
    JSONObject toJson(){
        JSONObject res = new JSONObject();
        try {
            res.put("title", title);
//            res.put("content", content);
            res.put("doc_id", doc_id);
            res.put("sent_id", sent_id);
            return res;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    Document(JSONObject obj){
        try {
            title = obj.getString("title");
            doc_id = obj.getString("doc_id");
            sent_id = obj.getString("sent_id");
            if (obj.has("content")) {
                content = obj.getString("content");
            }
            else {
                content = obj.getString("sent_ctx");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    static String genId(int length){
        final String SOURCES =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        char[] text = new char[length];
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            text[i] = SOURCES.charAt(random.nextInt(SOURCES.length()));
        }
        return new String(text);
    }
}

class DocumentRelation extends Document {
    ArrayList<Triple> triples = new ArrayList<>();

    void insert(Triple triple) {
        int index = -1;
        for (Triple next: triples) {
            if (triple.left_start < next.left_start ||
                    triple.left_start == next.left_start && triple.relation_start < next.right_start) {
                index = triples.indexOf(next);
                break;
            }
        }
        if (index > 0) {
            triples.add(index, triple);
        }
        else triples.add(triple);
    }
    void erase(String id) {
        int index = -1;
        for (Triple next: triples) {
            if (id.equals(next.id)) {
                index = triples.indexOf(next);
                break;
            }
        }
        if (index > 0) {
            triples.remove(index);
        }
    }

    @Override
    JSONObject toJson() {
        JSONObject res = super.toJson();
        try {
            res.put("sent_ctx", content);
            JSONArray tris = new JSONArray();
            for (int i = 0; i < triples.size(); i++){
                tris.put(triples.get(i).toJson());
            }
            res.put("triples", tris);
            return res;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    DocumentRelation(JSONObject obj) {
        super(obj);
        try {
            JSONArray array = obj.getJSONArray("triples");
            for (int i = 0; i < array.length(); i++) {
                JSONObject tri = array.getJSONObject(i);
                insert(new Triple(tri));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

class DocumentEntity extends Document {
    ArrayList<Entity> entities = new ArrayList<>();

//    void sortEntity(){
//        entities.sort(new Comparator<Entity>(){
//            @Override
//            public int compare(Entity o1, Entity o2) {
//                return o2.start - o1.start;
//            }
//        });
//    }

    void insert(Entity entity) {
        int index = -1;
        for (Entity next: entities) {
            if (entity.start < next.start) {
                index = entities.indexOf(next);
                break;
            }
        }
        if (index > 0) {
            entities.add(index, entity);
        }
        else entities.add(entity);
    }
    void erase(String id) {
        int index = -1;
        for (Entity next: entities) {
            if (id.equals(next.id)) {
                index = entities.indexOf(next);
                break;
            }
        }
        if (index > 0) {
            entities.remove(index);
        }
    }

    @Override
    JSONObject toJson() {
        JSONObject res = super.toJson();
        try {
            res.put("content", content);
            JSONArray ens = new JSONArray();
            for (int i = 0; i < entities.size(); i++){
                ens.put(entities.get(i).toJson());
            }
            res.put("entities", ens);
            return res;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    DocumentEntity(JSONObject obj) {
        super(obj);
    }
}

class Triple implements Serializable  {
    private static final long serialVersionUID = 1L;
    String leftGroupId, rightGroupId, relationGroupId;
    boolean checked = false;

    String id;
    int left_start = -1, left_end = -1,
            right_start = -1, right_end = -1,
            relation_start = -1, relation_end = -1,
            relation_id = 0;
    String left_entity;
    String right_entity;

    Triple(JSONObject obj) {
        try {
            id = obj.getString("id");
            left_entity = obj.getString("left_entity");
            right_entity = obj.getString("right_entity");
            left_start = obj.getInt("left_e_start");
            left_end = obj.getInt("left_e_end");
            right_start = obj.getInt("right_e_start");
            right_end = obj.getInt("right_e_end");
            relation_start = obj.getInt("relation_start");
            relation_end = obj.getInt("relation_end");
            relation_id = obj.getInt("relation_id");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    Triple(){
        id = "m" + Document.genId(19);
    }

    Triple(MarkTable.Group groupLeft, MarkTable.Group groupRight, MarkTable.Group groupRelation, int relationId){
        left_start = groupLeft.wordTokens.getFirst().start;
        left_end = groupLeft.wordTokens.getLast().end;
        left_entity = groupLeft.getEntityName();
        right_start = groupRight.wordTokens.getFirst().start;
        right_end = groupRight.wordTokens.getLast().end;
        right_entity = groupRight.getEntityName();
        if (groupRelation != null){
            relation_start = groupRelation.wordTokens.getFirst().start;
            relation_end = groupRelation.wordTokens.getLast().end;
            relationGroupId = groupRelation.id;
        }
        relation_id = relationId;
        leftGroupId = groupLeft.id;
        rightGroupId = groupRight.id;
        checked = true;
    }
    JSONObject toJson(){
        JSONObject res = new JSONObject();
        try {
            res.put("id", id);
            res.put("left_e_start", left_start);
            res.put("left_e_end", left_end);
            res.put("right_e_start", right_start);
            res.put("right_e_end", right_end);
            res.put("relation_start", relation_start);
            res.put("relation_end", relation_end);
            res.put("relation_id", relation_id);
            res.put("left_entity", left_entity);
            res.put("right_entity", right_entity);
            return res;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

class Entity implements Serializable  {
    private static final long serialVersionUID = 1L;
    String groupId;

    String id;
    String entityName = "";
    int start = -1;
    int end = -1;
    String nerTag = "PERSON";

    Entity(){
        id = "e" + Document.genId(19);
    }
    Entity(MarkTable.Group group, String nt){
        start = group.wordTokens.getFirst().start;
        end = group.wordTokens.getLast().end;
        entityName = group.getEntityName();
        nerTag = nt;
        groupId = group.id;
    }
    JSONObject toJson(){
        JSONObject res = new JSONObject();
        try {
            res.put("EntityName", entityName);
            res.put("Start", start);
            res.put("End", end);
            res.put("NerTag", nerTag);
            return res;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

class WordToken implements Serializable {
    private static final long serialVersionUID = 1L;

    String id;
    String word;
    int start;
    int end;

    WordToken(){
        id = "w" + Document.genId(19);
    }
    WordToken(JSONObject obj){
        try {
            word = obj.getString("word");
            if(obj.has("pos")){
                start = obj.getInt("pos");
                end = obj.getInt("pos") + obj.getInt("wlen");
                id = "w" + Document.genId(19);
            }
            else {
                start = obj.getInt("start");
                end = obj.getInt("end");
                id = obj.getString("id");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    static LinkedList<WordToken> Generate(JSONObject object){
        LinkedList<WordToken> res = new LinkedList<WordToken>();
        try {
            if (object.getInt("code") != 0) return res;
            JSONArray array = object.getJSONArray("tokens");
            for (int i = 0; i < array.length(); i++) {
                res.add(new WordToken(array.getJSONObject(i)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    JSONObject toJson(){
        JSONObject object = new JSONObject();
        try {
            object.put("word", word);
            object.put("start", start);
            object.put("end", end);
            object.put("id", id);
            return object;
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }

}

class FileHelper {
    void save(String filename, Object object){
        try{
            File file = new File(filename);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(object);
            oos.flush();
            oos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    Object read(String filename){
        try{
            File file = new File(filename);
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            Object object = ois.readObject();
            ois.close();
            return object;
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }
}
