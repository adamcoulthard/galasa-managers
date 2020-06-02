/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2020.
 */
/**
 * zOS File Manager - zOSMF Internal Implementation
 * 
 * @galasa.manager zOS File zOS MF
 * 
 * @galasa.release.state BETA - This Manager is feature complete but may contain known or unknown bugs.
 * 
 * @galasa.description
 * 
 * This Manager is the internal implementation of the zOS File Manager using zOS/MF. The z/OS MF File Manager is used in conjunction with the z/OS Manager. The z/OS Manager provides the interface for the z/OS file function and pulls in the z/OS MF File Manager to provide the implementation of the interface. If your test needs to instantiate a UNIX file, dataset, or VSAM data set, write and retrieve content from it, or configure and manipulate it then you can call the z/OS Manager in your test code and the z/OS Manager will call the z/OS MF File Manager to provide the implementation via the z/OS file function. 
 * <p>
 * See the <a href="/docs/managers/zos-manager">zOS Manager</a> for details of the z/OS File Annotations.
 */
package dev.galasa.zosfile.zosmf.manager.internal;