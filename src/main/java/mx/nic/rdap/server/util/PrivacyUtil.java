package mx.nic.rdap.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;

import mx.nic.rdap.core.catalog.RemarkType;
import mx.nic.rdap.core.catalog.Role;
import mx.nic.rdap.core.catalog.Status;
import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.RdapObject;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.server.catalog.PrivacyStatus;
import mx.nic.rdap.server.configuration.RdapConfiguration;
import mx.nic.rdap.server.listener.RdapInitializer;
import mx.nic.rdap.server.privacy.ObscuredPrivacy;
import mx.nic.rdap.server.privacy.PrivacySetting;
import mx.nic.rdap.server.privacy.PrivacySettingsFactory;

public class PrivacyUtil {

	private static Map<String, Map<String, PrivacySetting>> OBJECTS_PRIVACY_SETTING = new HashMap<>();

	// ***** Names of the properties files *****
	public static final String ENTITY = "entity";
	public static final String ENTITY_PUBLIC_ID = "entity_public_id";
	public static final String ENTITY_LINKS = "entity_links";
	public static final String ENTITY_REMARKS = "entity_remarks";
	public static final String ENTITY_EVENTS = "entity_events";

	public static final String VCARD = "vcard";

	public static final String DOMAIN = "domain";
	public static final String DOMAIN_PUBLIC_ID = "domain_public_id";
	public static final String DOMAIN_VARIANTS = "domain_variants";
	public static final String DOMAIN_LINKS = "domain_links";
	public static final String DOMAIN_REMARKS = "domain_remarks";
	public static final String DOMAIN_EVENTS = "domain_events";
	public static final String SECURE_DNS = "secure_dns";
	public static final String DS_DATA = "ds_data";
	public static final String KEY_DATA = "key_data";

	public static final String NAMESERVER = "nameserver";
	public static final String NAMESERVER_LINKS = "nameserver_links";
	public static final String NAMESERVER_REMARKS = "nameserver_remarks";
	public static final String NAMESERVER_EVENTS = "nameserver_events";

	public static final String AUTNUM = "autnum";
	public static final String AUTNUM_LINKS = "autnum_links";
	public static final String AUTNUM_REMARKS = "autnum_remarks";
	public static final String AUTNUM_EVENTS = "autnum_events";

	public static final String IP_NETWORK = "ip_network";
	public static final String IP_NETWORK_LINKS = "ip_network_links";
	public static final String IP_NETWORK_REMARKS = "ip_network_remarks";
	public static final String IP_NETWORK_EVENTS = "ip_network_events";

	private static final String DOMAIN_EVENTS_LINKS = "domain_events_links";
	private static final String DOMAIN_REMARKS_LINKS = "domain_remarks_links";
	private static final String KEY_DATA_EVENTS = "key_data_events";
	private static final String KEY_DATA_EVENTS_LINKS = "key_data_events_links";
	private static final String KEY_DATA_LINKS = "key_data_links";
	private static final String DS_DATA_EVENTS = "ds_data_events";
	private static final String DS_DATA_EVENTS_LINKS = "ds_data_events_links";
	private static final String DS_DATA_LINKS = "ds_data_links";

	private static final String NAMESERVER_EVENTS_LINKS = "nameserver_events_links";
	private static final String NAMESERVER_REMARKS_LINKS = "nameserver_remarks_links";

	private static final String IP_NETWORK_EVENTS_LINKS = "ip_network_events_links";
	private static final String IP_NETWORK_REMARKS_LINKS = "ip_network_remarks_links";

	private static final String AUTNUM_EVENTS_LINKS = "autnum_events_links";
	private static final String AUTNUM_REMARKS_LINKS = "autnum_remarks_links";

	private static final String ENTITY_EVENTS_LINKS = "entity_events_links";
	private static final String ENTITY_REMARKS_LINKS = "entity_remarks_links";

	private static final String ROLE_PRIVACY_STRING = "_ROLE_";

	// ***** End of names of the properties files *****

	/** Path where the default properties are read */
	private static final String DEFAULT_PATH = "META-INF/privacy_default/";

	/** Path where the user properties are read */
	private static final String USER_PATH = "WEB-INF/privacy/";

	public static void loadAllPrivacySettings() throws IOException {
		loadObjectPrivacySettings(ENTITY);
		for (Role role : Role.values()) {
			loadRolePrivacySettings(ENTITY, role);
		}

		loadObjectPrivacySettings(ENTITY_PUBLIC_ID);
		loadObjectPrivacySettings(ENTITY_LINKS);
		loadObjectPrivacySettings(ENTITY_EVENTS);
		loadObjectPrivacySettings(ENTITY_REMARKS);

		loadObjectPrivacySettings(VCARD);
		for (Role role : Role.values()) {
			loadRolePrivacySettings(VCARD, role);
		}

		loadObjectPrivacySettings(DOMAIN);
		loadObjectPrivacySettings(DOMAIN_PUBLIC_ID);
		loadObjectPrivacySettings(DOMAIN_VARIANTS);
		loadObjectPrivacySettings(DOMAIN_LINKS);
		loadObjectPrivacySettings(DOMAIN_EVENTS);
		loadObjectPrivacySettings(DOMAIN_REMARKS);
		loadObjectPrivacySettings(SECURE_DNS);
		loadObjectPrivacySettings(DS_DATA);
		loadObjectPrivacySettings(KEY_DATA);

		loadObjectPrivacySettings(NAMESERVER);
		loadObjectPrivacySettings(NAMESERVER_LINKS);
		loadObjectPrivacySettings(NAMESERVER_EVENTS);
		loadObjectPrivacySettings(NAMESERVER_REMARKS);

		loadObjectPrivacySettings(AUTNUM);
		loadObjectPrivacySettings(AUTNUM_LINKS);
		loadObjectPrivacySettings(AUTNUM_EVENTS);
		loadObjectPrivacySettings(AUTNUM_REMARKS);

		loadObjectPrivacySettings(IP_NETWORK);
		loadObjectPrivacySettings(IP_NETWORK_LINKS);
		loadObjectPrivacySettings(IP_NETWORK_EVENTS);
		loadObjectPrivacySettings(IP_NETWORK_REMARKS);

		loadObjectPrivacySettings(DOMAIN_EVENTS_LINKS);
		loadObjectPrivacySettings(DOMAIN_REMARKS_LINKS);
		loadObjectPrivacySettings(KEY_DATA_EVENTS);
		loadObjectPrivacySettings(KEY_DATA_EVENTS_LINKS);
		loadObjectPrivacySettings(KEY_DATA_LINKS);
		loadObjectPrivacySettings(DS_DATA_EVENTS);
		loadObjectPrivacySettings(DS_DATA_EVENTS_LINKS);
		loadObjectPrivacySettings(DS_DATA_LINKS);

		loadObjectPrivacySettings(NAMESERVER_EVENTS_LINKS);
		loadObjectPrivacySettings(NAMESERVER_REMARKS_LINKS);

		loadObjectPrivacySettings(IP_NETWORK_EVENTS_LINKS);
		loadObjectPrivacySettings(IP_NETWORK_REMARKS_LINKS);

		loadObjectPrivacySettings(AUTNUM_EVENTS_LINKS);
		loadObjectPrivacySettings(AUTNUM_REMARKS_LINKS);

		loadObjectPrivacySettings(ENTITY_EVENTS_LINKS);
		loadObjectPrivacySettings(ENTITY_REMARKS_LINKS);

	}

	private static boolean loadUserPrivacySettings(String fileName, Properties properties) throws IOException {
		ServletContext ctxt = RdapInitializer.getServletContext();
		Path path = null;
		InputStream inStream = null;
		if (ctxt != null) {
			String initParameter = ctxt.getInitParameter(RdapInitializer.PRIVACY_SETTINGS_PARAM_NAME);
			if (initParameter == null) {
				path = Paths.get(USER_PATH, fileName + ".properties");
				inStream = ctxt.getResourceAsStream(path.toString());
			} else {
				path = Paths.get(initParameter, fileName + ".properties");
				inStream = ctxt.getResourceAsStream(path.toString());
			}
		} else {
			path = Paths.get("META-INF/privacy/", fileName + ".properties");
			inStream = PrivacyUtil.class.getClassLoader().getResourceAsStream(path.toString());
		}

		boolean userFileExists = false;

		if (inStream != null) {
			try {
				properties.load(inStream);
			} catch (Exception e) {
				throw new IOException("Cannot load file: " + path.toString(), e);
			} finally {
				inStream.close();
			}

			userFileExists = true;
		}

		return userFileExists;
	}

	private static Properties loadDefaulProperties(String objectName) throws IOException {
		Properties properties;
		ClassLoader classLoader = PrivacyUtil.class.getClassLoader();
		try (InputStream in = classLoader.getResourceAsStream(DEFAULT_PATH + objectName + ".properties");) {
			properties = new Properties();
			properties.load(in);
		} catch (NullPointerException e) {
			throw new IOException("Cannot load file: " + DEFAULT_PATH + objectName + ".properties", e);
		}

		return properties;
	}

	private static void loadRolePrivacySettings(String objectName, Role role) throws IOException {
		// First load the default RedDog privacy settings
		Properties properties = loadDefaulProperties(objectName);
		// Override with the user "default" privacy settings if exists.
		loadUserPrivacySettings(objectName, properties);
		// Then override with the user "role specific" privacy setting for
		// objectName,
		// if exists.
		String objectRoleName = objectName + ROLE_PRIVACY_STRING + role.name();
		boolean userFileExists = loadUserPrivacySettings(objectRoleName, properties);

		// If "the user 'role specific' privacy file" not exists then don't put it on
		// the map.
		if (!userFileExists) {
			return;
		}

		HashMap<String, PrivacySetting> objectProperties = getPrivacyMap(properties, objectRoleName);
		OBJECTS_PRIVACY_SETTING.put(objectRoleName, Collections.unmodifiableMap(objectProperties));
	}

	private static void loadObjectPrivacySettings(String objectName) throws IOException {
		Properties properties = loadDefaulProperties(objectName);

		loadUserPrivacySettings(objectName, properties);

		HashMap<String, PrivacySetting> objectProperties = getPrivacyMap(properties, objectName);

		OBJECTS_PRIVACY_SETTING.put(objectName, Collections.unmodifiableMap(objectProperties));
	}

	private static HashMap<String, PrivacySetting> getPrivacyMap(Properties properties, String objectName) {
		HashMap<String, PrivacySetting> objectProperties = new HashMap<>();
		StringBuilder builder = new StringBuilder();
		boolean hasInvalidProperties = false;
		Set<Object> keySet = properties.keySet();
		for (Object keyObj : keySet) {
			String key = (String) keyObj;
			// There is no empty value.
			if (key.isEmpty()) {
				continue;
			}

			String property = properties.getProperty(key).trim();
			if (property.isEmpty()) {
				hasInvalidProperties = true;
				builder.append(key).append(" (must have a value); ");
				continue;
			}

			int indexOfPipe = property.indexOf('|');
			if (indexOfPipe > 0) {
				try {
					String privacy = property.substring(0, indexOfPipe).trim();
					String textToShow = property.substring(indexOfPipe + 1).trim();
					
					if (privacy.isEmpty() || textToShow.isEmpty() || !privacy.equalsIgnoreCase("obscured")) {
						hasInvalidProperties = true;
						builder.append(key).append("=").append(property).append(" (invalid obscured value); ");	
					}
					
					ObscuredPrivacy privacySetting = ObscuredPrivacy.create(textToShow);
					objectProperties.put(key, privacySetting);
					
				} catch (IndexOutOfBoundsException e) {
					hasInvalidProperties = true;
					builder.append(key).append("=").append(property).append(" (invalid obscured value); ");
				}
				continue;
			}

			if (!property.contains(",")) {
				try {
					PrivacyStatus privacyStatus = PrivacyStatus.valueOf(property.toUpperCase());
					objectProperties.put(key,
							PrivacySettingsFactory.getSetForRoles(privacyStatus.toString().toLowerCase()));
				} catch (IllegalArgumentException e) {
					// Can be a custom role, must be configured
					if (RdapConfiguration.isUserRoleConfigured(property)) {
						objectProperties.put(key, PrivacySettingsFactory.getSetForRoles(property.toLowerCase()));
					} else {
						hasInvalidProperties = true;
						builder.append(key).append("=").append(property).append(" (unknown value); ");
					}
				}
			} else {
				// List of roles, only "OWNER" can be mixed with custom roles
				boolean localError = false;
				int emptyRoles = 0;
				StringBuilder errorsList = new StringBuilder(key + "=");
				String[] privacyRoles = property.split(",");

				for (String privacyRole : privacyRoles) {
					privacyRole = privacyRole.trim();
					if (!privacyRole.isEmpty()) {
						try {
							PrivacyStatus privacyStatus = PrivacyStatus.valueOf(privacyRole.toUpperCase());
							if (!privacyStatus.equals(PrivacyStatus.OWNER)) {
								localError = true;
								errorsList.append(privacyRole).append(" (can't be mixed with custom roles), ");
							}
						} catch (IllegalArgumentException e) {
							// Can be a custom role, must be configured
							if (!RdapConfiguration.isUserRoleConfigured(privacyRole)) {
								localError = true;
								errorsList.append(privacyRole).append(" (unknown value), ");
							}
						}
					} else {
						emptyRoles++;
					}
				}
				if (emptyRoles == privacyRoles.length) {
					localError = true;
					errorsList.append("(can't be an empty list); ");
				}
				if (localError) {
					hasInvalidProperties = true;
					builder.append(errorsList.substring(0, errorsList.toString().length() - 2)).append("; ");
				} else {
					objectProperties.put(key, PrivacySettingsFactory.getSetForRoles(privacyRoles));
				}
			}
		}

		if (hasInvalidProperties) {
			throw new RuntimeException("Invalid privacy file '" + objectName + ".properties'.\n Invalid values: "
					+ builder.substring(0, builder.toString().length() - 2));
		}

		return objectProperties;
	}

	public static Map<String, PrivacySetting> getEntityPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(ENTITY);
	}

	public static Map<String, PrivacySetting> getEntityPrivacySettings(Role role) {
		return OBJECTS_PRIVACY_SETTING.get(ENTITY + ROLE_PRIVACY_STRING + role.name());
	}

	public static Map<String, PrivacySetting> getEntityPublicIdsPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(ENTITY_PUBLIC_ID);
	}

	public static Map<String, PrivacySetting> getNameserverPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(NAMESERVER);
	}

	public static Map<String, PrivacySetting> getDomainPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(DOMAIN);
	}

	public static Map<String, PrivacySetting> getDomainPublicIdsPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(DOMAIN_PUBLIC_ID);
	}

	public static Map<String, PrivacySetting> getDomainVariantsPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(DOMAIN_VARIANTS);
	}

	public static Map<String, PrivacySetting> getSecureDnsPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(SECURE_DNS);
	}

	public static Map<String, PrivacySetting> getDsDataPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(DS_DATA);
	}

	public static Map<String, PrivacySetting> getKeyDataPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(KEY_DATA);
	}

	public static Map<String, PrivacySetting> getEntityLinkPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(ENTITY_LINKS);
	}

	public static Map<String, PrivacySetting> getEntityRemarkPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(ENTITY_REMARKS);
	}

	public static Map<String, PrivacySetting> getEntityEventPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(ENTITY_EVENTS);
	}

	public static Map<String, PrivacySetting> getDomainLinkPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(DOMAIN_LINKS);
	}

	public static Map<String, PrivacySetting> getDomainRemarkPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(DOMAIN_REMARKS);
	}

	public static Map<String, PrivacySetting> getDomainEventPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(DOMAIN_EVENTS);
	}

	public static Map<String, PrivacySetting> getNameserverLinkPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(NAMESERVER_LINKS);
	}

	public static Map<String, PrivacySetting> getNameserverRemarkPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(NAMESERVER_REMARKS);
	}

	public static Map<String, PrivacySetting> getNameserverEventPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(NAMESERVER_EVENTS);
	}

	public static Map<String, PrivacySetting> getAutnumPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(AUTNUM);
	}

	public static Map<String, PrivacySetting> getAutnumLinkPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(AUTNUM_LINKS);
	}

	public static Map<String, PrivacySetting> getAutnumRemarkPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(AUTNUM_REMARKS);
	}

	public static Map<String, PrivacySetting> getAutnumEventPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(AUTNUM_EVENTS);
	}

	public static Map<String, PrivacySetting> getIpNetworkPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(IP_NETWORK);
	}

	public static Map<String, PrivacySetting> getIpNetworkLinkPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(IP_NETWORK_LINKS);
	}

	public static Map<String, PrivacySetting> getIpNetworkRemarkPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(IP_NETWORK_REMARKS);
	}

	public static Map<String, PrivacySetting> getIpNetworkEventPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(IP_NETWORK_EVENTS);
	}

	public static Map<String, PrivacySetting> getVCardPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(VCARD);
	}

	public static Map<String, PrivacySetting> getVCardPrivacySettings(Role role) {
		return OBJECTS_PRIVACY_SETTING.get(VCARD + ROLE_PRIVACY_STRING + role.name());
	}

	public static Map<String, PrivacySetting> getDomainEventsLinksPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(DOMAIN_EVENTS_LINKS);
	}

	public static Map<String, PrivacySetting> getDomainRemarksLinksPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(DOMAIN_REMARKS_LINKS);
	}

	public static Map<String, PrivacySetting> getKeyDataEventsPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(KEY_DATA_EVENTS);
	}

	public static Map<String, PrivacySetting> getKeyDataEventsLinksPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(KEY_DATA_EVENTS_LINKS);
	}

	public static Map<String, PrivacySetting> getKeyDataLinksPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(KEY_DATA_LINKS);
	}

	public static Map<String, PrivacySetting> getDsDataEventsPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(DS_DATA_EVENTS);
	}

	public static Map<String, PrivacySetting> getDsDataEventsLinksPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(DS_DATA_EVENTS_LINKS);
	}

	public static Map<String, PrivacySetting> getDsDataLinksPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(DS_DATA_LINKS);
	}

	public static Map<String, PrivacySetting> getNameserverEventsLinksPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(NAMESERVER_EVENTS_LINKS);
	}

	public static Map<String, PrivacySetting> getNameserverRemarksLinksPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(NAMESERVER_REMARKS_LINKS);
	}

	public static Map<String, PrivacySetting> getIpNetworkEventsLinksPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(IP_NETWORK_EVENTS_LINKS);
	}

	public static Map<String, PrivacySetting> getIpNetworkRemarksLinksPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(IP_NETWORK_REMARKS_LINKS);
	}

	public static Map<String, PrivacySetting> getAutnumEventsLinksPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(AUTNUM_EVENTS_LINKS);
	}

	public static Map<String, PrivacySetting> getAutnumRemarksLinksPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(AUTNUM_REMARKS_LINKS);
	}

	public static Map<String, PrivacySetting> getEntityEventsLinksPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(ENTITY_EVENTS_LINKS);
	}

	public static Map<String, PrivacySetting> getEntityRemarksLinksPrivacySettings() {
		return OBJECTS_PRIVACY_SETTING.get(ENTITY_REMARKS_LINKS);
	}

	/**
	 * Adds a {@link Remark} of type {@link RemarkType}
	 * <code>OBJECT_AUTHORIZATION</code>, and if the <code>rdapObject</code> is an
	 * instance of {@link Entity} also adds the {@link Status} <code>Private</code>.
	 * 
	 * @param rdapObject
	 *            The object to modify
	 */
	public static void addPrivacyRemarkAndStatus(RdapObject rdapObject) {
		if (rdapObject.getRemarks() == null) {
			rdapObject.setRemarks(new ArrayList<Remark>());
		}
		rdapObject.getRemarks().add(new Remark(RemarkType.OBJECT_AUTHORIZATION));
		if (rdapObject instanceof Entity) {
			if (rdapObject.getStatus() == null) {
				rdapObject.setStatus(new ArrayList<Status>());
			}
			rdapObject.getStatus().add(Status.PRIVATE);
		} else if (rdapObject instanceof Domain) {
			if (RdapConfiguration.addEmailRemark())
				addEmailRedactedForPrivacy(rdapObject);
		}
	}
	
	/* Rdap response profile feb-19 2.7.5.3 
	 * https://www.icann.org/en/system/files/files/rdap-response-profile-15feb19-en.pdf
	 */
	private static void addEmailRedactedForPrivacy(RdapObject rdapObject) {
		if (rdapObject.getRemarks() == null)
			rdapObject.setRemarks(new ArrayList<>());

		Remark r = new Remark();
		r.setTitle("EMAIL REDACTED FOR PRIVACY");
		r.setType("object redacted due to authorization.");

		RemarkDescription rd = new RemarkDescription();
		rd.setDescription("Please query the RDDS service of the Registrar of Record identified in this output");
		rd.setOrder(1);
		rd.setRemarkId(1L);

		RemarkDescription rd2 = new RemarkDescription();
		rd2.setDescription("for information on how to contact the Registrant of the queried domain name.");
		rd2.setOrder(2);
		rd2.setRemarkId(2L);

		r.getDescriptions().add(rd);
		r.getDescriptions().add(rd2);

		rdapObject.getRemarks().add(r);
	}

	/**
	 * @return <code>true</code> if the user is owner of the RdapObject
	 */
	public static boolean isSubjectOwner(String userName, RdapObject object) {
		if (userName == null || userName.isEmpty() || object == null) {
			return false;
		}

		if (object instanceof Entity) {
			Entity ent = (Entity) object;
			if (ent.getHandle() == null || ent.getHandle().isEmpty()) {
				return false;
			}
			return ent.getHandle().equalsIgnoreCase(userName);
		}

		for (Entity ent : object.getEntities()) {
			if (isEntityOwner(userName, object, ent)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return <code>true</code> if the user is owner of the Entity Object
	 */
	private static boolean isEntityOwner(String userName, RdapObject father, Entity ent) {
		if (ent.getHandle() == null || !ent.getHandle().equalsIgnoreCase(userName)) {
			return false;
		}

		if (ent.getRoles() == null || ent.getRoles().isEmpty()) {
			return false;
		}

		for (Role role : ent.getRoles()) {
			if (RdapConfiguration.isRoleAnOwner(father, role)) {
				return true;
			}
		}

		return false;
	}
}
