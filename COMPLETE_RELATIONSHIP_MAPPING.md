# Complete JPA Relationship Mapping Reference

## All 6 Relationships in Your Project - Detailed Breakdown

---

## RELATIONSHIP 1: USER ↔ PROPERTY (One-to-Many)

### What It Does:
One user can own multiple properties. Each property belongs to exactly one user.

### Code Implementation:

**User.java:**
```java
package com.realestate.model;

@Entity
@Table(name = "users")
@Data
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String firstName;
    private String lastName;
    private String email;
    
    // ONE-TO-MANY: One user can have many properties
    @OneToMany(
        mappedBy = "owner",           // Property field name
        cascade = CascadeType.ALL,    // Cascade operations
        fetch = FetchType.LAZY        // Load only when accessed
    )
    @JsonIgnoreProperties({"owner"})  // Prevent infinite JSON recursion
    private List<Property> properties = new ArrayList<>();
}
```

**Property.java:**
```java
package com.realestate.model;

@Entity
@Table(name = "properties")
@Data
@Builder
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private BigDecimal price;
    private String address;
    
    // MANY-TO-ONE: Many properties belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "owner_id",            // Foreign key column name in DB
        nullable = true               // Property can exist without owner
    )
    @JsonIgnoreProperties({"properties"})
    private User owner;
}
```

### Database Schema:
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(100),
    ...
);

CREATE TABLE properties (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    price DECIMAL(15, 2),
    address VARCHAR(255),
    owner_id BIGINT,  -- FOREIGN KEY
    ...
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL
);
```

### Usage Example:
```java
// Create user
User john = User.builder()
    .firstName("John")
    .lastName("Smith")
    .email("john@example.com")
    .build();

// Create properties
Property property1 = Property.builder()
    .title("Villa Mumbai")
    .price(new BigDecimal("5000000"))
    .owner(john)  // Set owner
    .build();

Property property2 = Property.builder()
    .title("Apartment Delhi")
    .price(new BigDecimal("3000000"))
    .owner(john)  // Set owner
    .build();

// Add to collection
john.getProperties().add(property1);
john.getProperties().add(property2);

// Save - properties automatically saved due to cascade
userRepository.save(john);
```

### Retrieve Example:
```java
// Get user with all properties (lazy loaded on access)
User user = userRepository.findById(1L).get();

// This triggers loading of properties
List<Property> myProperties = user.getProperties();

// Get all properties of user via SQL
SELECT * FROM properties WHERE owner_id = 1;
```

---

## RELATIONSHIP 2: PROPERTY ↔ PROPERTY_IMAGE (One-to-Many)

### What It Does:
One property can have multiple images. Each image belongs to exactly one property.

### Code Implementation:

**Property.java:**
```java
@Entity
@Table(name = "properties")
@Data
@Builder
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    // ONE-TO-MANY: One property has many images
    @OneToMany(
        mappedBy = "property",        // PropertyImage field name
        cascade = CascadeType.ALL,    // Delete images when property deleted
        orphanRemoval = true,         // Delete if removed from collection
        fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties({"property"})
    private List<PropertyImage> images = new ArrayList<>();
    
    // Helper methods for bi-directional relationship
    public void addImage(PropertyImage image) {
        images.add(image);
        image.setProperty(this);  // Maintain both sides
    }
    
    public void removeImage(PropertyImage image) {
        images.remove(image);
        image.setProperty(null);  // Maintain both sides
    }
}
```

**PropertyImage.java:**
```java
@Entity
@Table(name = "property_images")
@Data
@Builder
public class PropertyImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String imageUrl;
    private String caption;
    private Boolean isPrimary;
    private Integer displayOrder;
    private LocalDateTime uploadedAt;
    
    // MANY-TO-ONE: Many images belong to one property
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "property_id",         // Foreign key column
        nullable = false              // Image must have property
    )
    private Property property;
}
```

### Database Schema:
```sql
CREATE TABLE properties (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255),
    ...
);

CREATE TABLE property_images (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    image_url VARCHAR(255),
    caption VARCHAR(500),
    is_primary BOOLEAN,
    display_order INT,
    property_id BIGINT NOT NULL,  -- FOREIGN KEY
    ...
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);
```

### Usage Example:
```java
// Create property
Property villa = Property.builder()
    .title("Beautiful Villa")
    .price(new BigDecimal("5000000"))
    .build();

// Create images
PropertyImage image1 = PropertyImage.builder()
    .imageUrl("villa_front.jpg")
    .isPrimary(true)
    .displayOrder(1)
    .build();

PropertyImage image2 = PropertyImage.builder()
    .imageUrl("villa_pool.jpg")
    .isPrimary(false)
    .displayOrder(2)
    .build();

// Add images to property
villa.addImage(image1);  // Helper method maintains both sides
villa.addImage(image2);

// Save property - images automatically saved with cascade
propertyRepository.save(villa);
```

### Retrieve Example:
```java
// Get property with images
Property property = propertyRepository.findById(101L).get();

// Get all images (lazy loaded)
List<PropertyImage> images = property.getImages();

// Get primary image
PropertyImage primary = property.getImages().stream()
    .filter(PropertyImage::getIsPrimary)
    .findFirst()
    .orElse(null);
```

---

## RELATIONSHIP 3: BUILDER_GROUP ↔ PROPERTY (One-to-Many)

### What It Does:
One builder group can develop multiple properties. Each property belongs to one builder group.

### Code Implementation:

**BuilderGroup.java:**
```java
@Entity
@Table(name = "builder_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuilderGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String name;  // e.g., "Shapoorji Pallonji", "TCG"
    
    private String description;
    private Boolean active;
    
    // ONE-TO-MANY: One builder group has many properties
    @OneToMany(
        mappedBy = "builderGroup",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY
    )
    private List<Property> properties = new ArrayList<>();
}
```

**Property.java:**
```java
@Entity
@Table(name = "properties")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    // MANY-TO-ONE: Many properties belong to one builder group
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "builder_group_id",    // Foreign key column
        nullable = true               // Property can be independent
    )
    private BuilderGroup builderGroup;
}
```

### Database Schema:
```sql
CREATE TABLE builder_groups (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE,
    description VARCHAR(1000),
    active BOOLEAN,
    ...
);

CREATE TABLE properties (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255),
    builder_group_id BIGINT,  -- FOREIGN KEY
    ...
    FOREIGN KEY (builder_group_id) REFERENCES builder_groups(id)
);
```

### Usage Example:
```java
// Create builder group
BuilderGroup sp = BuilderGroup.builder()
    .name("Shapoorji Pallonji")
    .description("Premium real estate developer")
    .active(true)
    .build();

// Create properties
Property property1 = Property.builder()
    .title("Premium Tower Mumbai")
    .builderGroup(sp)
    .build();

Property property2 = Property.builder()
    .title("Grandeur Bangalore")
    .builderGroup(sp)
    .build();

// Save builder group (properties saved with cascade)
builderGroupRepository.save(sp);
```

### Filter by Builder Group:
```java
// Find all properties by specific builder group
List<Property> spProperties = propertyRepository
    .findByBuilderGroup(sp);

// Get builder group with all properties
BuilderGroup group = builderGroupRepository.findById(1L).get();
List<Property> allProperties = group.getProperties();
```

---

## RELATIONSHIP 4: USER ↔ SUBSCRIPTION (One-to-One)

### What It Does:
Each user has exactly one subscription. Each subscription belongs to exactly one user.

### Code Implementation:

**User.java:**
```java
@Entity
@Table(name = "users")
@Data
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String firstName;
    private String lastName;
    private String email;
    
    private SubscriptionType subscriptionType;  // PREMIUM, FREE, etc.
    
    // ONE-TO-ONE: One user has one subscription
    @OneToOne(
        mappedBy = "user",            // Subscription field name
        cascade = CascadeType.ALL,    // Cascade operations
        fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties({"user"})
    private Subscription subscription;
}
```

**Subscription.java:**
```java
@Entity
@Table(name = "subscriptions")
@Data
@Builder
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private SubscriptionType planType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal price;
    private Boolean active;
    private Boolean autoRenew;
    
    // ONE-TO-ONE: One subscription belongs to one user
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        unique = true                 // CRITICAL: Ensures 1:1 relationship
    )
    @JsonIgnoreProperties({"subscription"})
    private User user;
}
```

### Why UNIQUE Constraint?
```
Database table constraint:
UNIQUE (user_id) on subscriptions table

Effect:
- user_id = 1 can appear only once
- user_id = 2 can appear only once
- Prevents: User having 2 subscriptions
- Enforces: 1:1 relationship at DB level

Examples of valid data:
┌─────────────────────┐
│ id | user_id | plan │
├─────────────────────┤
│ 1  │   1     │ PRE  │  ✅ Valid
│ 2  │   2     │ FREE │  ✅ Valid
│ 3  │   3     │ PRE  │  ✅ Valid
└─────────────────────┘

Invalid (would violate unique constraint):
│ 1  │   1     │ PRE  │  
│ 2  │   1     │ FREE │  ❌ Same user_id twice!
```

### Database Schema:
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100),
    subscription_type ENUM('PREMIUM', 'FREE'),
    ...
);

CREATE TABLE subscriptions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,  -- UNIQUE ensures 1:1
    plan_type ENUM('PREMIUM', 'FREE'),
    start_date DATE,
    end_date DATE,
    price DECIMAL(10, 2),
    active BOOLEAN,
    auto_renew BOOLEAN,
    ...
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### Usage Example:
```java
// Create user
User user = User.builder()
    .firstName("John")
    .lastName("Smith")
    .email("john@example.com")
    .build();

// Create subscription
Subscription subscription = Subscription.builder()
    .user(user)
    .planType(SubscriptionType.PREMIUM)
    .startDate(LocalDate.now())
    .endDate(LocalDate.now().plusMonths(1))
    .price(new BigDecimal("999"))
    .active(true)
    .autoRenew(true)
    .build();

// Link both sides (even though only subscription has FK)
user.setSubscription(subscription);

// Save user (subscription saved with cascade)
userRepository.save(user);
```

### Retrieve Example:
```java
// Get user's subscription
User user = userRepository.findById(1L).get();
Subscription subscription = user.getSubscription();

// Check if subscription is premium
if (subscription.getPlanType() == SubscriptionType.PREMIUM) {
    // Premium features available
}
```

---

## RELATIONSHIP 5: USER ↔ FAVORITE (One-to-Many via Junction)

### What It Does:
One user can favorite multiple properties. Implemented through a Favorite junction table.

### Code Implementation:

**User.java:**
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ONE-TO-MANY: One user has many favorites
    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties({"user"})
    private List<Favorite> favorites = new ArrayList<>();
}
```

**Favorite.java (Junction Table):**
```java
@Entity
@Table(
    name = "favorites",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "property_id"})
    }
)
@Data
@Builder
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Many favorites belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"favorites"})
    private User user;
    
    // Many favorites reference one property
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    @JsonIgnoreProperties({"owner", "images"})
    private Property property;
    
    private LocalDateTime createdAt;
    private String notes;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

### Database Schema:
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    ...
);

CREATE TABLE properties (
    id BIGINT PRIMARY KEY,
    ...
);

CREATE TABLE favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    property_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    notes VARCHAR(1000),
    UNIQUE (user_id, property_id),  -- Prevent duplicate favorites
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);
```

### Usage Example:
```java
// Add to favorites
User user = userRepository.findById(1L).get();
Property property = propertyRepository.findById(101L).get();

Favorite favorite = Favorite.builder()
    .user(user)
    .property(property)
    .notes("Great investment opportunity")
    .build();

// Save favorite
favoriteRepository.save(favorite);
```

### Query Examples:
```java
// Get user's favorite properties
User user = userRepository.findById(1L).get();
List<Property> favorites = user.getFavorites().stream()
    .map(Favorite::getProperty)
    .collect(Collectors.toList());

// Check if property is favorited by user
boolean isFavorite = user.getFavorites().stream()
    .anyMatch(f -> f.getProperty().getId().equals(propertyId));

// Get users who favorited a property
List<User> users = favoriteRepository.findByProperty(property)
    .stream()
    .map(Favorite::getUser)
    .collect(Collectors.toList());
```

---

## RELATIONSHIP 6: USER ↔ SEARCH_HISTORY (One-to-Many)

### What It Does:
One user can have multiple search history records. Each search belongs to one user.

### Code Implementation:

**User.java:**
```java
@Entity
@Table(name = "users")
public class User {
    // ONE-TO-MANY: One user has many search histories
    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties({"user"})
    private List<SearchHistory> searchHistories = new ArrayList<>();
}
```

**SearchHistory.java:**
```java
@Entity
@Table(name = "search_histories")
@Data
@Builder
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"searchHistories"})
    private User user;
    
    private String searchKey;  // Search query
    private String results;    // Serialized search results
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

### Usage Example:
```java
// Record a search
User user = userRepository.findById(1L).get();

SearchHistory search = SearchHistory.builder()
    .user(user)
    .searchKey("apartments in mumbai")
    .results("Found 25 properties")
    .build();

searchHistoryRepository.save(search);

// Get user's search history
List<SearchHistory> history = user.getSearchHistories();
```

---

## 📊 Summary Table of All Relationships

| # | From | To | Type | FK Location | Cardinality | Annotations |
|---|------|----|----|---|---|---|
| 1 | User | Property | 1:N | properties.owner_id | 1→∞ | @OneToMany, @ManyToOne |
| 2 | Property | PropertyImage | 1:N | property_images.property_id | 1→∞ | @OneToMany, @ManyToOne |
| 3 | BuilderGroup | Property | 1:N | properties.builder_group_id | 1→∞ | @OneToMany, @ManyToOne |
| 4 | User | Subscription | 1:1 | subscriptions.user_id(UNIQUE) | 1↔1 | @OneToOne |
| 5 | User | Favorite | 1:N | favorites.user_id | 1→∞ | @OneToMany (via junction) |
| 6 | User | SearchHistory | 1:N | search_histories.user_id | 1→∞ | @OneToMany, @ManyToOne |

---

## 🎯 Key Takeaways

1. **One-to-Many / Many-to-One are the SAME relationship** viewed from different sides
2. **Foreign Key always goes on the MANY side** (child table)
3. **One-to-One needs UNIQUE constraint** to ensure each side has only one
4. **Cascade operations** propagate parent changes to children
5. **orphanRemoval** deletes children removed from parent collection
6. **Junction tables** (like Favorite) implement Many-to-Many relationships

