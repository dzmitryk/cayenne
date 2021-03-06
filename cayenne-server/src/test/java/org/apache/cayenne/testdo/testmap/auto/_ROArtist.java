package org.apache.cayenne.testdo.testmap.auto;

import java.sql.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;
import org.apache.cayenne.testdo.testmap.Painting;

/**
 * Class _ROArtist was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _ROArtist extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String ARTIST_NAME_PROPERTY = "artistName";
    @Deprecated
    public static final String DATE_OF_BIRTH_PROPERTY = "dateOfBirth";
    @Deprecated
    public static final String PAINTING_ARRAY_PROPERTY = "paintingArray";

    public static final String ARTIST_ID_PK_COLUMN = "ARTIST_ID";

    public static final Property<String> ARTIST_NAME = new Property<String>("artistName");
    public static final Property<Date> DATE_OF_BIRTH = new Property<Date>("dateOfBirth");
    public static final Property<List<Painting>> PAINTING_ARRAY = new Property<List<Painting>>("paintingArray");

    public String getArtistName() {
        return (String)readProperty("artistName");
    }

    public Date getDateOfBirth() {
        return (Date)readProperty("dateOfBirth");
    }

    public void addToPaintingArray(Painting obj) {
        addToManyTarget("paintingArray", obj, true);
    }
    public void removeFromPaintingArray(Painting obj) {
        removeToManyTarget("paintingArray", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Painting> getPaintingArray() {
        return (List<Painting>)readProperty("paintingArray");
    }


}
