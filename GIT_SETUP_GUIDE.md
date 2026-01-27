# Git Setup & Push Guide

## Issue: `git push origin main` - "src refspec main does not match any"

This error occurs when the `main` branch doesn't exist locally. Here's how to fix it:

---

## Solution 1: Using Main Branch (Recommended)

### Step 1: Check Current Branch
```bash
git branch
# Should show current branch (e.g., master or main)
```

### Step 2: If Current Branch is Master, Rename to Main
```bash
git branch -m master main
# This renames master to main locally
```

### Step 3: Create Main on Remote
```bash
git push -u origin main
# This creates main on GitHub and sets tracking
```

---

## Solution 2: Using Master Branch

If you want to keep using master:

```bash
# Push master branch
git push -u origin master

# Or if origin/master already exists:
git push origin master
```

---

## Complete Setup Steps

### Initial Setup (First Time)

```bash
# Navigate to project
cd "D:\CDAC Project\Atharva\Atharva\real-estate-backend"

# Check git status
git status

# Add all files
git add .

# Commit changes
git commit -m "Add new features: Registration, Agent Login, Favorites, Viewings, Images, Builder Groups"

# List branches
git branch -a

# If you see only master:
git branch -m master main

# Push to GitHub
git push -u origin main
```

### Future Pushes

```bash
# After making changes
git add .
git commit -m "Your commit message"
git push origin main  # Or just: git push
```

---

## Step-by-Step for Your Project

```powershell
# PowerShell commands
cd "D:\CDAC Project\Atharva\Atharva\real-estate-backend"

# Check current state
git status

# View branches
git branch -a

# If master exists, rename to main
git branch -m master main

# Stage all new files
git add .

# Check what will be committed
git status

# Commit with descriptive message
git commit -m "feat: Add 6 new features - Registration, Agent Login, Favorites with Pagination, Schedule Viewing, Property Images, Builder Groups Filter"

# Push to GitHub
git push -u origin main

# Verify push was successful
git log --oneline -5
```

---

## Common Issues & Solutions

### Issue 1: "fatal: 'origin' does not appear to be a git repository"
```bash
# Solution: Initialize git
git init
git remote add origin https://github.com/YOUR_USERNAME/real-estate-backend.git
git branch -M main
git push -u origin main
```

### Issue 2: "Permission denied (publickey)"
```bash
# Solution: Use HTTPS instead of SSH
git remote set-url origin https://github.com/YOUR_USERNAME/real-estate-backend.git
git push -u origin main
```

### Issue 3: "Branch 'main' does not exist"
```bash
# Solution: Create and push main branch
git branch -m master main
git push -u origin main
```

---

## What to Push

You're pushing:

### New Files (14 files)
- DTOs: RegistrationRequest, AgentLoginResponse, FavoriteResponse, PropertyImageRequest, PropertyImageResponse, BuilderGroupRequest, BuilderGroupResponse
- Entity: BuilderGroup
- Services: PropertyImageService, BuilderGroupService
- Controllers: PropertyImageController, BuilderGroupController
- Repository: BuilderGroupRepository

### Modified Files (11 files)
- AuthController (added registration & agent login)
- Property model (added builderGroup)
- PropertyRepository (builder filtering)
- PropertyResponse (builderGroup field)
- FavoriteService, FavoriteRepository, FavoriteController (pagination)
- ScheduleViewingService, ScheduleViewingRepository, ScheduleViewingController (pagination)
- PropertyImageRepository (added query method)

### Documentation (4 files)
- NEW_FEATURES_GUIDE.md
- API_TESTING_GUIDE.md
- IMPLEMENTATION_SUMMARY.md
- QUICK_START_FEATURES.md

---

## Commit Message Template

```
feat: Add 6 new features to real estate backend

ADDED:
- User registration form (BUYER and AGENT types)
- Agent-specific login endpoint
- Add to favorites with pagination support
- Schedule property viewings with status management
- Property image management system
- Builder groups filtering (e.g., TCG, Shapoorji Pallonji)

MODIFIED:
- Enhanced FavoriteService with pagination
- Enhanced ScheduleViewingService with pagination
- Updated Property model with builderGroup relationship
- Updated AuthController with new authentication endpoints

FILES CREATED: 14 new files
FILES MODIFIED: 11 existing files
NEW ENDPOINTS: 40+ API endpoints
DOCUMENTATION: Complete feature guides and testing documentation

This implements all requested features with full CRUD operations,
validation, pagination, and comprehensive documentation.
```

---

## After Successful Push

1. ✅ Code is on GitHub
2. ✅ Team can see your work
3. ✅ Create a Pull Request (if needed)
4. ✅ Deploy to production (when ready)

---

## Useful Git Commands

```powershell
# View commit log
git log --oneline

# View current branch
git branch

# View all branches (local + remote)
git branch -a

# Create a new branch (for feature development)
git checkout -b feature/new-feature

# Switch to main
git checkout main

# Pull latest changes from main
git pull origin main

# Check what's changed
git diff

# View specific commit
git show <commit-hash>

# Undo last commit (keep changes)
git reset --soft HEAD~1

# Create a tag (for releases)
git tag v1.1.0
git push origin v1.1.0
```

---

## PowerShell Tips

```powershell
# If git commands don't work, ensure Git is in PATH
# Or use full path:
"C:\Program Files\Git\bin\git.exe" status

# Check Git version
git --version

# Check Git configuration
git config --list

# Set Git config (first time)
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

---

## Troubleshooting

### Get detailed error info
```bash
git push -v origin main
# Shows detailed verbose output
```

### Check remote configuration
```bash
git remote -v
# Shows remote URLs
```

### Reset to match remote
```bash
git fetch origin
git reset --hard origin/main
```

---

## Summary

Your code is ready to push! Use the commands in "Step-by-Step for Your Project" section above.

If you get any errors:
1. Run `git status` to see current state
2. Run `git log --oneline` to see commit history
3. Check remote: `git remote -v`
4. Try the appropriate solution from "Common Issues" section

---

**Status:** 🟢 Ready to Push
**Last Updated:** January 27, 2026
