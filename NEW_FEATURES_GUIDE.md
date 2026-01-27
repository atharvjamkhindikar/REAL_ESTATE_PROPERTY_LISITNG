# Real Estate Backend - New Features Implementation Guide

This document describes all the new features added to the real estate backend application.

## ✅ Features Implemented

### 1. **User Registration** ✓
#### Endpoints:
- `POST /api/auth/register` - Register a new user (BUYER or AGENT)

#### Request:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "password123",
  "confirmPassword": "password123",
  "phone": "555-1234",
  "userType": "BUYER",
  "licenseNumber": "RE-12345",  // Required only for AGENT
  "company": "ABC Properties",  // Optional
  "bio": "Professional real estate agent"  // Optional
}
```

#### Features:
- Password confirmation validation
- License number validation for AGENT type (format: XX-12345)
- Email uniqueness check
- Returns full UserResponse with user details

#### Response:
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "phone": "555-1234",
    "userType": "BUYER",
    "role": "USER",
    "subscriptionType": "FREE",
    "active": true,
    "createdAt": "2026-01-27T10:00:00"
  }
}
```

---

### 2. **Agent Login** ✓
#### Endpoints:
- `POST /api/auth/agent-login` - Agent-specific login

#### Request:
```json
{
  "email": "agent@example.com",
  "password": "password123"
}
```

#### Features:
- Validates user type is AGENT
- Returns agent-specific details (company, license number)
- Full agent profile information
- Account status check

#### Response:
```json
{
  "success": true,
  "message": "Agent login successful",
  "data": {
    "id": 2,
    "email": "agent@example.com",
    "firstName": "Jane",
    "lastName": "Smith",
    "phone": "555-5678",
    "company": "Elite Properties",
    "licenseNumber": "RE-67890",
    "bio": "Specializing in luxury homes",
    "profileImageUrl": "https://...",
    "role": "USER",
    "subscriptionType": "BASIC",
    "active": true,
    "createdAt": "2026-01-27T10:00:00"
  }
}
```

---

### 3. **Add to Favorites** ✓
#### Endpoints:
- `GET /api/favorites/user/{userId}` - Get all favorites (non-paginated)
- `GET /api/favorites/user/{userId}/paged` - Get favorites with pagination
- `POST /api/favorites` - Add property to favorites
- `DELETE /api/favorites` - Remove from favorites
- `DELETE /api/favorites/{favoriteId}` - Remove favorite by ID
- `POST /api/favorites/toggle` - Toggle favorite status
- `PATCH /api/favorites/{favoriteId}/notes` - Update favorite notes
- `GET /api/favorites/check` - Check if property is favorited
- `GET /api/favorites/count/{propertyId}` - Get favorite count for property

#### Paginated Request:
```
GET /api/favorites/user/1/paged?page=0&size=10&sortBy=createdAt&direction=DESC
```

#### Add Favorite Request:
```json
POST /api/favorites?userId=1&propertyId=1&notes="Beautiful house"
```

#### Features:
- Add properties to favorites with optional notes
- Pagination support with sorting
- Toggle favorite status (add/remove)
- Check if property is already favorited
- Get favorite count for properties
- Update notes on favorites

#### Response:
```json
{
  "success": true,
  "message": "Property added to favorites",
  "data": {
    "content": [
      {
        "id": 1,
        "userId": 1,
        "propertyId": 5,
        "notes": "Beautiful house",
        "createdAt": "2026-01-27T10:00:00",
        "property": {
          "id": 5,
          "title": "Modern House",
          "address": "123 Main St",
          "city": "New York",
          "state": "NY",
          "price": 450000,
          "imageUrl": "https://..."
        }
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 25,
    "totalPages": 3,
    "first": true,
    "last": false,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

---

### 4. **Schedule Viewing** ✓
#### Endpoints:
- `POST /api/schedule-viewings` - Schedule a property viewing
- `GET /api/schedule-viewings/user/{userId}` - Get user's viewings
- `GET /api/schedule-viewings/user/{userId}/paged` - Get user's viewings (paginated)
- `GET /api/schedule-viewings/property/{propertyId}` - Get property viewings
- `PUT /api/schedule-viewings/{id}/confirm` - Confirm viewing (Agent)
- `PUT /api/schedule-viewings/{id}/reject` - Reject viewing with reason (Agent)
- `PUT /api/schedule-viewings/{id}/complete` - Mark viewing as completed
- `PUT /api/schedule-viewings/{id}/cancel` - Cancel viewing

#### Schedule Viewing Request:
```json
{
  "userId": 1,
  "propertyId": 5,
  "viewingDate": "2026-02-15",
  "viewingTime": "14:30",
  "notes": "Looking for family home with backyard"
}
```

#### Update Status Request:
```json
PUT /api/schedule-viewings/1/confirm
PUT /api/schedule-viewings/1/reject?rejectionReason=Property sold
```

#### Features:
- Schedule viewings for future dates only
- Automatic conflict detection for same-day viewings
- Viewing status workflow: PENDING → CONFIRMED/REJECTED → COMPLETED/CANCELLED
- Agent can confirm, reject (with reason), or complete viewings
- Users can cancel their viewings
- Pagination support for user viewings
- Timestamps for all status changes

#### Response:
```json
{
  "success": true,
  "message": "Viewing scheduled successfully",
  "data": {
    "id": 1,
    "userId": 1,
    "propertyId": 5,
    "viewingDate": "2026-02-15",
    "viewingTime": "14:30:00",
    "status": "PENDING",
    "notes": "Looking for family home with backyard",
    "createdAt": "2026-01-27T10:00:00"
  }
}
```

---

### 5. **Add Images to Property** ✓
#### Endpoints:
- `POST /api/properties/{propertyId}/images` - Add image to property
- `GET /api/properties/{propertyId}/images` - Get all images for property
- `PUT /api/properties/{propertyId}/images/{imageId}` - Update image details
- `DELETE /api/properties/{propertyId}/images/{imageId}` - Delete image
- `PATCH /api/properties/{propertyId}/images/{imageId}/primary` - Set as primary image
- `POST /api/properties/{propertyId}/images/reorder` - Reorder images

#### Add Image Request:
```json
{
  "imageUrl": "https://example.com/image1.jpg",
  "caption": "Living room view",
  "isPrimary": true,
  "displayOrder": 0
}
```

#### Reorder Request:
```json
POST /api/properties/5/images/reorder
[1, 3, 2, 4]  // Array of image IDs in desired order
```

#### Features:
- Add multiple images per property
- Set primary image for property
- Add captions to images
- Auto-increment display order
- Reorder images easily
- Update image details
- Delete images
- Images returned ordered by displayOrder

#### Response:
```json
{
  "success": true,
  "message": "Image added successfully",
  "data": {
    "id": 1,
    "imageUrl": "https://example.com/image1.jpg",
    "caption": "Living room view",
    "isPrimary": true,
    "displayOrder": 0,
    "uploadedAt": "2026-01-27T10:00:00"
  }
}
```

---

### 6. **Builder Groups (Filter by Builder)** ✓
#### Endpoints:
- `GET /api/builder-groups` - Get all builder groups
- `GET /api/builder-groups/active` - Get active builder groups only
- `GET /api/builder-groups/{id}` - Get specific builder group
- `POST /api/builder-groups` - Create builder group (Admin)
- `PUT /api/builder-groups/{id}` - Update builder group
- `DELETE /api/builder-groups/{id}` - Delete builder group

#### Properties Filter by Builder:
- `GET /api/properties?builderGroupId=1` - Search properties by builder group
- Properties include builderGroup information in response

#### Create Builder Group Request:
```json
{
  "name": "Shapoorji Pallonji",
  "description": "Luxury residential properties across major cities",
  "active": true
}
```

#### Features:
- Create and manage builder groups
- Filter properties by builder group
- View properties by specific developer/builder
- Example builders: TCG, Shapoorji Pallonji, DLF, Lodha
- Active/inactive status for builders
- Track property count per builder

#### Response:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Shapoorji Pallonji",
    "description": "Luxury residential properties",
    "active": true,
    "propertyCount": 15,
    "createdAt": "2026-01-27T10:00:00",
    "updatedAt": "2026-01-27T10:00:00"
  }
}
```

#### Properties with Builder Group:
```json
{
  "data": {
    "id": 5,
    "title": "Luxury Apartment",
    "price": 500000,
    "city": "Mumbai",
    "builderGroup": {
      "id": 1,
      "name": "Shapoorji Pallonji",
      "description": "Luxury residential properties"
    },
    "imageUrls": ["https://...", "https://..."],
    "favoriteCount": 12,
    "isFavorited": true
  }
}
```

---

## 📋 Database Changes

### New Tables:
1. **builder_groups** - Stores builder/developer information

### New Columns in Existing Tables:
- **properties**: Added `builder_group_id` (Foreign Key)

### Repository Updates:
- PropertyRepository: Added builder group filtering methods
- FavoriteRepository: Added pagination support
- ScheduleViewingRepository: Added pagination support
- PropertyImageRepository: Added display order query
- New: BuilderGroupRepository

---

## 🔌 Integration Guide

### 1. Update Frontend to Use New APIs:

**Registration:**
```javascript
POST /api/auth/register
```

**Agent Login:**
```javascript
POST /api/auth/agent-login
```

**Add Favorites:**
```javascript
POST /api/favorites?userId=1&propertyId=5&notes="Love this property"
GET /api/favorites/user/1/paged?page=0&size=10
```

**Schedule Viewing:**
```javascript
POST /api/schedule-viewings
PUT /api/schedule-viewings/1/confirm
```

**Images:**
```javascript
POST /api/properties/5/images
PATCH /api/properties/5/images/1/primary
```

**Builder Filter:**
```javascript
GET /api/builder-groups
GET /api/properties?builderGroupId=1
```

---

## ✨ Key Features Summary

| Feature | Status | Endpoints | Key Methods |
|---------|--------|-----------|-------------|
| User Registration | ✓ | 1 | register() |
| Agent Login | ✓ | 1 | agentLogin() |
| Add to Favorites | ✓ | 8 | addFavorite(), toggleFavorite(), getUserFavoritesPaged() |
| Schedule Viewing | ✓ | 8+ | scheduleViewing(), confirmViewing(), getUserViewingsPaged() |
| Images | ✓ | 6 | addImage(), setPrimaryImage(), reorderImages() |
| Builder Groups | ✓ | 6 | createBuilderGroup(), searchByBuilder() |

---

## 🚀 Next Steps

1. **Test all endpoints** using Postman or curl
2. **Update frontend forms** to use new registration endpoint
3. **Add builder group filter** to property search UI
4. **Implement image upload** (currently uses URL strings)
5. **Add password hashing** using BCrypt for production
6. **Implement JWT tokens** for secure authentication
7. **Add role-based access control** (RBAC) for agent-only endpoints

---

## 📝 Notes

- All timestamps are in LocalDateTime format (ISO 8601)
- Pagination defaults: page=0, size=10, sortBy=createdAt, direction=DESC
- License number format validation: ^[A-Z]{2}-\d{5}$ (e.g., RE-12345)
- Viewing dates must be in the future
- Password confirmation is required during registration
- Agents require license number and company information

---

**Last Updated:** January 27, 2026
**Version:** 1.1.0
