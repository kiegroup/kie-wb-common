# MaskedTextBox Field - Deployment Document

## Overview
This document provides detailed deployment instructions for the MaskedTextBox field implementation in kie-wb-common, including build process, JAR file details, and deployment procedures for existing JBPM 7.74.1.Final installations.

## Build Process

### Prerequisites
- **Java:** JDK 8 or 11
- **Maven:** 3.6.0 or higher
- **Memory:** 4GB+ RAM recommended
- **Disk Space:** 2GB+ free space

### Environment Setup
```bash
# Set Maven memory options
export MAVEN_OPTS="-Xmx4g -Xms1g -XX:MaxPermSize=512m"

# Verify Java version
java -version

# Verify Maven version
mvn -version
```

### Building kie-wb-common

#### 1. Full Clean Build
```bash
cd kie-wb-common
mvn clean install -DskipTests
```

**Build Time:** ~45-60 minutes  
**Output:** JAR files in local Maven repository (`~/.m2/repository`)

#### 2. Build with Tests
```bash
mvn clean install
```

**Build Time:** ~60-75 minutes  
**Additional Output:** Test reports in `target/surefire-reports/`

#### 3. Fast Build (Development I have followed this)
```bash
# Skip tests and documentation
mvn clean install -DskipTests -Dmaven.javadoc.skip=true

# Use parallel builds
mvn clean install -T 4 -DskipTests
```

**Build Time:** ~30-40 minutes

## Updated JAR Files

### 1. Core MaskedTextBox JAR
**JAR Name:** `kie-wb-common-forms-fields-7.74.1.Final.jar`  
**Location:** `~/.m2/repository/org/kie/workbench/forms/kie-wb-common-forms-fields/7.74.1.Final/`  

**Contains:**
- `AbstractMaskedTextBoxFieldDefinition.class`
- `MaskedTextBoxFieldDefinition.class`
- `MaskedTextBoxFieldProvider.class`
- `MaskedTextBoxFieldDefinitionTest.class`

**Maven Coordinates:**
```xml
<dependency>
    <groupId>org.kie.workbench.forms</groupId>
    <artifactId>kie-wb-common-forms-fields</artifactId>
    <version>7.74.1.Final</version>
</dependency>
```

### 2. Backend Services JAR
**JAR Name:** `kie-wb-common-forms-backend-services-7.74.1.Final.jar`  
**Location:** `~/.m2/repository/org/kie/workbench/forms/kie-wb-common-forms-backend-services/7.74.1.Final/`  

**Modified Classes:**
- `BackendFieldManagerImpl.class` (added MaskedTextBox registration)

### 3. Client Services JAR
**JAR Name:** `kie-wb-common-dynamic-forms-client-7.74.1.Final.jar`  
**Location:** `~/.m2/repository/org/kie/workbench/forms/kie-wb-common-dynamic-forms-client/7.74.1.Final/`  

**Modified Classes:**
- `ClientFieldManagerImpl.class` (added MaskedTextBox registration)

### 4. Form Editor JAR
**JAR Name:** `kie-wb-common-forms-editor-client-7.74.1.Final.jar`  
**Location:** `~/.m2/repository/org/kie/workbench/forms/kie-wb-common-forms-editor-client/7.74.1.Final/`  

**Modified Classes:**
- `EditorFieldTypesProviderImpl.class` (added MaskedTextBox to palette)

## Extra Files and Resources

### 1. Documentation Files
**Location:** `kie-wb-common/`
- `MASKED_TEXTBOX_TECHNICAL_IMPLEMENTATION.md` (this file)
- `MASKED_TEXTBOX_DEPLOYMENT.md` (deployment guide)

### 2. Test Resources
**Location:** Various `src/test/java/` directories
- Unit test classes and test data
- Surefire test reports in `target/surefire-reports/`

### 3. Maven Build Files
**Generated during build:**
- `target/` directories with compiled classes
- `pom.xml` files (no modifications to existing files)

## Deployment to Existing JBPM 7.74.1.Final

### Option 1: Maven Repository Deployment (Recommended)

#### Step 1: Install to Local Repository
```bash
# Build and install to local Maven repository
cd kie-wb-common
mvn clean install -DskipTests

# Verify installation
ls ~/.m2/repository/org/kie/workbench/forms/kie-wb-common-forms-fields/7.74.1.Final/
```

#### Step 2: Update Dependent Projects
The updated JARs will automatically be used by any project that depends on kie-wb-common components when you rebuild those projects.

### Option 2: Direct JAR Replacement

#### Step 1: Locate Existing JARs
```bash
# Find existing JBPM installation
JBPM_HOME=/path/to/jbpm-7.74.1.Final

# Locate JAR files in WildFly deployment
find $JBPM_HOME -name "kie-wb-common-forms-fields-*.jar"
find $JBPM_HOME -name "kie-wb-common-forms-*-services-*.jar"
```

#### Step 2: Backup Existing JARs
```bash
# Create backup directory
mkdir $JBPM_HOME/jars-backup-$(date +%Y%m%d)

# Backup original JARs
cp $JBPM_HOME/standalone/deployments/business-central.war/WEB-INF/lib/kie-wb-common-forms-*.jar \
   $JBPM_HOME/jars-backup-$(date +%Y%m%d)/
```

#### Step 3: Replace JARs
```bash
# Copy updated JARs
cp ~/.m2/repository/org/kie/workbench/forms/kie-wb-common-forms-fields/7.74.1.Final/kie-wb-common-forms-fields-7.74.1.Final.jar \
   $JBPM_HOME/standalone/deployments/business-central.war/WEB-INF/lib/

# Repeat for other modified JARs
cp ~/.m2/repository/org/kie/workbench/forms/kie-wb-common-forms-backend-services/7.74.1.Final/kie-wb-common-forms-backend-services-7.74.1.Final.jar \
   $JBPM_HOME/standalone/deployments/business-central.war/WEB-INF/lib/

# Continue for all updated JARs...
```

### Option 3: Complete WAR Rebuild (Most Reliable)

#### Step 1: Build Complete Business Central WAR
```bash
# Build kie-wb-common first
cd kie-wb-common
mvn clean install -DskipTests

# Build jbpm-wb (requires kie-wb-common)
cd ../jbpm-wb  
mvn clean install -DskipTests

# Build final Business Central WAR
cd ../kie-wb-distributions/business-central-parent/business-central-distribution-wars/business-central
mvn clean install -DskipTests
```

#### Step 2: Deploy New WAR
```bash
# Stop JBPM server
$JBPM_HOME/bin/standalone.sh --connect --command=:shutdown

# Backup existing deployment
mv $JBPM_HOME/standalone/deployments/business-central.war \
   $JBPM_HOME/standalone/deployments/business-central.war.backup.$(date +%Y%m%d)

# Deploy new WAR
cp target/business-central-7.74.1.Final-wildfly23.war \
   $JBPM_HOME/standalone/deployments/business-central.war

# Start JBPM server
$JBPM_HOME/bin/standalone.sh
```

## Verification Steps

### 1. Build Verification
```bash
# Verify JAR contents
jar -tf ~/.m2/repository/org/kie/workbench/forms/kie-wb-common-forms-fields/7.74.1.Final/kie-wb-common-forms-fields-7.74.1.Final.jar | grep -i masked

# Expected output:
# org/kie/workbench/common/forms/fields/shared/fieldTypes/basic/maskedTextBox/definition/AbstractMaskedTextBoxFieldDefinition.class
# org/kie/workbench/common/forms/fields/shared/fieldTypes/basic/maskedTextBox/definition/MaskedTextBoxFieldDefinition.class
# org/kie/workbench/common/forms/fields/shared/fieldTypes/basic/maskedTextBox/provider/MaskedTextBoxFieldProvider.class
```

### 2. Deployment Verification
```bash
# Check server startup logs
tail -f $JBPM_HOME/standalone/log/server.log

# Look for successful deployment messages
# Should see: "Deployed business-central.war"
# Should NOT see: ClassNotFoundException or similar errors
```

### 3. Functional Verification
1. **Access Business Central:** http://localhost:8080/business-central
2. **Create Test Project:** Design → Projects → New Project
3. **Create Form:** Add User Task → Generate Form
4. **Verify MaskedTextBox:** Check Field Type dropdown includes "MaskedTextBox"

## Maintenance

### 1. Version Updates
When updating to newer JBPM versions:
1. Update version numbers in all Maven coordinates
2. Rebuild all components
3. Test compatibility with new JBPM version
4. Update documentation

### 2. Monitoring
Monitor these aspects after deployment:
- Server startup logs for errors
- Form editor functionality
- MaskedTextBox field creation and configuration
- Runtime form behavior

### 3. Backup Strategy
Maintain backups of:
- Original JAR files before modification
- Complete WAR files before deployment
- Build scripts and documentation
- Test data and procedures

## Support Information

### 1. Build Environment
- **Tested with:** Maven 3.8.1, OpenJDK 11, Windows 10/Linux
- **Build Duration:** 45-90 minutes depending on system and options
- **Required Memory:** 4GB+ RAM, 2GB+ disk space

### 2. Deployment Environment  
- **Compatible with:** JBPM 7.74.1.Final on WildFly 23
- **Tested Deployment Methods:** Complete WAR rebuild, JAR replacement
- **Database Compatibility:** All JBPM-supported databases

### 3. Known Limitations
- Requires complete application server restart after JAR replacement
- Maven local repository must be accessible during build
- GWT compilation increases build time significantly

This deployment document provides comprehensive guidance for successfully building and deploying the MaskedTextBox field implementation in existing JBPM 7.74.1.Final environments.
