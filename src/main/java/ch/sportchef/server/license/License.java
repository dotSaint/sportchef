package ch.sportchef.server.license;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class License {

    private final String shortName ;
    private final String longName;
    private final String shortVersion;
    private final String longVersion;
    private final String text;
    private final String link;

    private License() {
        // Jackson deserialization
        this(null, null, null, null, null, null);
    }

    public License(final String shortName, final String longName, final String shortVersion, final String longVersion, final String text, final String link) {
        this.shortName = shortName;
        this.longName = longName;
        this.shortVersion = shortVersion;
        this.longVersion = longVersion;
        this.text = text;
        this.link = link;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortVersion() {
        return shortVersion;
    }

    public String getLongVersion() {
        return longVersion;
    }

    public String getText() {
        return text;
    }

    public String getLink() {
        return link;
    }
}
