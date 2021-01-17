/**
 * TeleStax, Open Source Cloud Communications  Copyright 2012. 
 * and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.mobicents.gmlc.slee;

import java.io.*;

import java.nio.charset.Charset;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.EventContext;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.facilities.Tracer;
import javax.slee.resource.ResourceAdaptorTypeID;

import net.java.slee.resource.http.events.HttpServletRequestEvent;

import org.mobicents.gmlc.MscPropertiesManagement;
//import org.mobicents.gmlc.slee.mlp.MLPException;
//import org.mobicents.gmlc.slee.mlp.MLPRequest;
//import org.mobicents.gmlc.slee.mlp.MLPResponse;
import org.mobicents.protocols.ss7.indicator.NatureOfAddress;
import org.mobicents.protocols.ss7.indicator.NumberingPlan;
import org.mobicents.protocols.ss7.indicator.RoutingIndicator;

import org.mobicents.protocols.ss7.map.api.MAPDialog;
import org.mobicents.protocols.ss7.map.api.MAPMessage;
import org.mobicents.protocols.ss7.map.api.MAPMessageType;


import org.mobicents.protocols.ss7.map.api.MAPApplicationContext;
import org.mobicents.protocols.ss7.map.api.MAPApplicationContextName;
import org.mobicents.protocols.ss7.map.api.MAPApplicationContextVersion;
import org.mobicents.protocols.ss7.map.api.MAPException;
import org.mobicents.protocols.ss7.map.api.MAPParameterFactory;
import org.mobicents.protocols.ss7.map.api.MAPProvider;
import org.mobicents.protocols.ss7.map.api.errors.MAPErrorCode;
import org.mobicents.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.mobicents.protocols.ss7.map.api.dialog.MAPUserAbortChoice;
import org.mobicents.protocols.ss7.map.api.primitives.AddressNature;
import org.mobicents.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;
import org.mobicents.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdFixedLength;
import org.mobicents.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;
import org.mobicents.protocols.ss7.map.api.primitives.IMEI;
import org.mobicents.protocols.ss7.map.api.primitives.IMSI;

import org.mobicents.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer;

import org.mobicents.protocols.ss7.map.api.primitives.SubscriberIdentity;
import org.mobicents.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.RequestedInfo;

import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.GPRSMSClass;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.GeodeticInformation;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.GeographicalInformation;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationEPS;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationGPRS;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationNumberMap;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.MNPInfoRes;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.MSClassmark2;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.NotReachableReason;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.PSSubscriberState;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberInfo;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberState;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberStateChoice;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.UserCSGInformation;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.LSAIdentity;
import org.mobicents.protocols.ss7.map.api.service.sms.MAPDialogSms;
import org.mobicents.protocols.ss7.map.MAPParameterFactoryImpl;



import org.mobicents.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.mobicents.protocols.ss7.map.primitives.SubscriberIdentityImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.RequestedInfoImpl;
import org.mobicents.protocols.ss7.sccp.impl.parameter.ParameterFactoryImpl;
import org.mobicents.protocols.ss7.sccp.parameter.GlobalTitle;
import org.mobicents.protocols.ss7.sccp.parameter.ParameterFactory;
import org.mobicents.protocols.ss7.sccp.parameter.SccpAddress;
import org.mobicents.protocols.ss7.map.api.primitives.AddressString;
import org.mobicents.slee.SbbContextExt;
import org.mobicents.slee.resource.map.MAPContextInterfaceFactory;

import org.mobicents.slee.resource.map.events.MAPEvent;

import org.mobicents.protocols.ss7.map.api.service.supplementary.ActivateSSRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.ActivateSSResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.DeactivateSSRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.DeactivateSSResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.EraseSSRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.EraseSSResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.GetPasswordRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.GetPasswordResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.InterrogateSSRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.InterrogateSSResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.MAPDialogSupplementary;
import org.mobicents.protocols.ss7.map.api.service.supplementary.ProcessUnstructuredSSRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.ProcessUnstructuredSSResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.RegisterPasswordRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.RegisterPasswordResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.RegisterSSRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.RegisterSSResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.UnstructuredSSNotifyRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.UnstructuredSSNotifyResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.UnstructuredSSRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.UnstructuredSSResponse;
import org.mobicents.protocols.ss7.map.api.datacoding.CBSDataCodingScheme;
import org.mobicents.protocols.ss7.map.datacoding.CBSDataCodingSchemeImpl;

import org.mobicents.protocols.ss7.map.api.primitives.USSDString;
import org.mobicents.protocols.ss7.map.primitives.ISDNAddressStringImpl;

import org.mobicents.protocols.ss7.tcap.api.MessageType;

import net.java.slee.resource.http.HttpServletRaActivityContextInterfaceFactory;
import net.java.slee.resource.http.HttpServletRaSbbInterface;
import net.java.slee.resource.http.HttpSessionActivity;
import net.java.slee.resource.http.events.HttpServletRequestEvent;

import org.mobicents.ussdgateway.XmlMAPDialog;
import javolution.util.FastList;
import javolution.xml.stream.XMLStreamException;

import org.mobicents.ussdgateway.EventsSerializeFactory;


/**
 * 
 * @author amit bhayani
 * @author sergey vetyutnev
 */
public abstract class MobileCoreNetworkMscInterfaceSbb implements Sbb {

	private static final int EVENT_SUSPEND_TIMEOUT = 1000 * 60 * 3;
	private static final String CONTENT_MAIN_TYPE = "text";
	private static final String CONTENT_SUB_TYPE = "xml";
	private static final String CONTENT_TYPE = CONTENT_MAIN_TYPE + "/" + CONTENT_SUB_TYPE;
	
	protected SbbContextExt sbbContext;

	private Tracer logger;

	protected MAPContextInterfaceFactory mapAcif;
	protected MAPProvider mapProvider;
	protected MAPParameterFactory mapParameterFactory;
	protected ParameterFactory sccpParameterFact;

	protected static final ResourceAdaptorTypeID mapRATypeID = new ResourceAdaptorTypeID("MAPResourceAdaptorType",
			"org.mobicents", "2.0");
	protected static final String mapRaLink = "MAPRA2";


	// -------------------------------------------------------------
	// HTTP Server RA STUFF
	// -------------------------------------------------------------
	protected static final ResourceAdaptorTypeID httpServerRATypeID = new ResourceAdaptorTypeID(
			"HttpServletResourceAdaptorType", "org.mobicents", "1.0");
	protected static final String httpServerRaLink = "HttpServletRA";

	protected HttpServletRaSbbInterface httpServletProvider;
	protected HttpServletRaActivityContextInterfaceFactory httpServletRaActivityContextInterfaceFactory;



	private static final MscPropertiesManagement mscPropertiesManagement = MscPropertiesManagement.getInstance();

	private SccpAddress gmlcSCCPAddress = null;
	private MAPApplicationContext anyTimeEnquiryContext = null;

	private EventsSerializeFactory eventsSerializeFactory = null;
    /**
     * HTTP Request Types (GET or MLP)
     */
    private enum HttpRequestType {
        REST("rest"),
        OPT("opt"),
        UNSUPPORTED("404");

        private String path;

        HttpRequestType(String path) {
            this.path = path;
        }

        public String getPath() {
            return String.format("/ussd/%s", path);
        }

        public static HttpRequestType fromPath(String path) {
            for (HttpRequestType type: values()) {
                if (path.equals(type.getPath())) {
                    return type;
                }
            }

            return UNSUPPORTED;
        }
    }

    
    
    
    /**
     * Request
     */
    private class HttpRequest implements Serializable {
        HttpRequestType type;
        String msisdn;

        public HttpRequest(HttpRequestType type, String msisdn) {
            this.type = type;
            this.msisdn = msisdn;
        }

        public HttpRequest(HttpRequestType type) {
            this(type, "");
        }
    }

    /**
     * Response Location
     */
    private class CGIResponse implements Serializable {
	String text = null; 
        String x = "-1";
        String y = "-1";
        String radius = "-1";
        int cell = -1;
        int mcc = -1;
        int mnc = -1;
        int lac = -1;
        int aol = -1;
        String vlr = "-1";
    }

    /**
     * For debugging - fake location data
     */
    private String fakeNumber = "19395550113";
    private String fakeLocationAdditionalInfoErrorString = "Internal positioning failure occurred";
    private int fakeCellId = 300;
    private String fakeLocationX = "27 28 25.00S";
    private String fakeLocationY = "153 01 43.00E";
    private String fakeLocationRadius = "5000";

	/** Creates a new instance of CallSbb */
	public MobileCoreNetworkMscInterfaceSbb() {
	}

	public void setSbbContext(SbbContext sbbContext) {
		this.sbbContext = (SbbContextExt) sbbContext;
		this.logger = sbbContext.getTracer(MobileCoreNetworkMscInterfaceSbb.class.getSimpleName());
		try {
			this.mapAcif = (MAPContextInterfaceFactory) this.sbbContext.getActivityContextInterfaceFactory(mapRATypeID);
			this.mapProvider = (MAPProvider) this.sbbContext.getResourceAdaptorInterface(mapRATypeID, mapRaLink);
			this.mapParameterFactory = this.mapProvider.getMAPParameterFactory();
			this.sccpParameterFact = new ParameterFactoryImpl();

			this.httpServletRaActivityContextInterfaceFactory = (HttpServletRaActivityContextInterfaceFactory) this.sbbContext.getActivityContextInterfaceFactory(httpServerRATypeID);
			this.httpServletProvider = (HttpServletRaSbbInterface) this.sbbContext.getResourceAdaptorInterface(httpServerRATypeID, httpServerRaLink);
	

			//this.logger.info("setSbbContext() complete");
		} catch (Exception ne) {
			logger.severe("Could not set SBB context:", ne);
		}
	}

	public void unsetSbbContext() {
		this.sbbContext = null;
		this.logger = null;
	}

	public void sbbCreate() throws CreateException {
		if (this.logger.isFineEnabled()) {
			this.logger.fine("Created KnowledgeBase");
		}
	}

	public void sbbPostCreate() throws CreateException {

	}

	public void sbbActivate() {
	}

	public void sbbPassivate() {
	}

	public void sbbLoad() {
	}

	public void sbbStore() {
	}

	public void sbbRemove() {
	}

	public void sbbExceptionThrown(Exception exception, Object object, ActivityContextInterface activityContextInterface) {
	}

	public void sbbRolledBack(RolledBackContext rolledBackContext) {
	}
	
	
	
	
	

	/**
	 * DIALOG Events
	 */

	/*
public void onDialogRequest(org.mobicents.slee.resource.map.events.DialogRequest evt, ActivityContextInterface aci) {
//		if (logger.isInfoEnabled()) {
//			this.logger.info("New MAP Dialog. Received event MAPOpenInfo " + evt);
//		}

		MAPDialog mapDialog = evt.getMAPDialog();

//		logger.info("mapDialog.getLocalAddress="+mapDialog.getLocalAddress());
//		logger.info("mapDialog.getRemoteAddress="+mapDialog.getRemoteAddress());


//		XmlMAPDialog dialog = new XmlMAPDialog(mapDialog.getApplicationContext(), mapDialog.getLocalAddress(),
//				mapDialog.getRemoteAddress(), mapDialog.getLocalDialogId(), mapDialog.getRemoteDialogId(),
//				evt.getDestReference(), evt.getOrigReference());
//		dialog.setReturnMessageOnError(mapDialog.getReturnMessageOnError());
//		dialog.setTCAPMessageType(mapDialog.getTCAPMessageType());
//		dialog.setNetworkId(mapDialog.getNetworkId());

//		this.setDialog(dialog);
	}
	*/






	public void onDialogTimeout(org.mobicents.slee.resource.map.events.DialogTimeout evt, ActivityContextInterface aci) {
        this.logger.severe("\nRx :  onDialogTimeout " + evt);



		this.handleHttpErrorResponse("DialogTimeout");
	}

	
	public void onDialogDelimiter(org.mobicents.slee.resource.map.events.DialogDelimiter event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
        this.logger.info("\nReceived onDialogDelimiter = " + event);
     //   MAPDialogSms mapDialog = (MAPDialogSms)this.getDialog();
        
     //   logger.info(mapDialog.getUserObject().toString());
		//	try {
//		        if (flag == true) {
//		        	mapDialog.send();
//		        }
//		        else {
//		        	mapDialog.close(false);
//		        }
			//} catch (MAPException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
	//		}
        
	}

	public void onDialogAccept(org.mobicents.slee.resource.map.events.DialogAccept event, ActivityContextInterface aci) {
        //if (this.logger.isFineEnabled()) {
            this.logger.info("\nReceived onDialogAccept = " + event);
        //}
	}

	public void onDialogReject(org.mobicents.slee.resource.map.events.DialogReject event, ActivityContextInterface aci) {
        this.logger.severe("\nRx :  onDialogReject " + event);

        //HLR this.handleLocationResponse(MLPResponse.MLPResultType.SYSTEM_FAILURE, null, "DialogReject: " + event);
	}

	public void onDialogUserAbort(org.mobicents.slee.resource.map.events.DialogUserAbort event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
		this.logger.severe("onDialogUserAbort " + event);
		XmlMAPDialog xmlMAPDialog = this.getXmlMAPDialog();
		if (xmlMAPDialog != null) {
			try {
				xmlMAPDialog.reset();
				xmlMAPDialog.abort(event.getUserReason());
				xmlMAPDialog.setTCAPMessageType(event.getMAPDialog().getTCAPMessageType());
				this.abortHttpDialog(xmlMAPDialog);
			} catch( MAPException e) {
				logger.severe("Error while handling DialogUserAbort", e);
			}
		}

        //HLR this.handleLocationResponse(MLPResponse.MLPResultType.SYSTEM_FAILURE, null, "DialogUserAbort: " + event);
	}

	public void onDialogProviderAbort(org.mobicents.slee.resource.map.events.DialogProviderAbort event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
        this.logger.severe("onDialogProviderAbort " + event);

       //HLR this.handleLocationResponse(MLPResponse.MLPResultType.SYSTEM_FAILURE, null, "DialogProviderAbort: " + event);
	}

	public void onDialogClose(org.mobicents.slee.resource.map.events.DialogClose event, ActivityContextInterface aci) {
     //   if (this.logger.isFineEnabled()) {
            this.logger.info("onDialogClose = " + event);
      //  }
	}

	public void onDialogNotice(org.mobicents.slee.resource.map.events.DialogNotice event, ActivityContextInterface aci) {
            this.logger.info("onDialogNotice = " + event);
	}

	public void onDialogRelease(org.mobicents.slee.resource.map.events.DialogRelease event, ActivityContextInterface aci) {
            this.logger.info("onDialogRelease = " + event);
	}

	/**
	 * Component Events
	 */
	public void onInvokeTimeout(org.mobicents.slee.resource.map.events.InvokeTimeout event, ActivityContextInterface aci) {
      //  if (this.logger.isFineEnabled()) {
            this.logger.info("Received onInvokeTimeout = " + event);
        //}
	}

	public void onErrorComponent(org.mobicents.slee.resource.map.events.ErrorComponent event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
//        if (this.logger.isFineEnabled()) {
            this.logger.info("Received onErrorComponent = " + event);
  //      }

		MAPErrorMessage mapErrorMessage = event.getMAPErrorMessage();
		long error_code = mapErrorMessage.getErrorCode().longValue();

        this.handleHttpErrorResponse( "ReturnError: " + String.valueOf(error_code) + " : "
                        + event.getMAPErrorMessage());
	}

	public void onRejectComponent(org.mobicents.slee.resource.map.events.RejectComponent event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
        this.logger.severe("Rx :  onRejectComponent " + event);

        this.handleHttpErrorResponse("RejectComponent: " + event);
	}

	


	public void onProcessUnstructuredSSResponse(ProcessUnstructuredSSResponse event, ActivityContextInterface aci,
			EventContext eventContext) {
		//if (logger.isFineEnabled())
			logger.info("Received ProcessUnstructuredSSResponse " + event);

		this.processReceivedMAPEvent((MAPEvent) event);

	}


	// gateway has responded with menu
	// this end is going to send single choice of menu
public void onUnstructuredSSRequest(UnstructuredSSRequest event, ActivityContextInterface aci,
			EventContext eventContext) 
{
       //  MLPResponse.MLPResultType result;
	 CGIResponse response = new CGIResponse();
         String mlpClientErrorMessage = null;

    try {
	response.text= event.getUSSDString().getString(null);
	logger.info("Rx UnstructuredSSRequest Indication. USSD String=" + event.getUSSDString().getString(null));
	
   } catch (MAPException e) {
		e.printStackTrace();
   }

	
	//	this.setUnstructuredSSRequest(event);
	// keep it off	this.handleLocationResponse( response, null);
	//	logger.info("stored evt = " + this.getUnstructuredSSRequest());

	// this method assumes the xml dialog has been setup during sending a processussdrequest message
	// in that case, ussdgateway sends this request
	// however there is another case where ussdgateway send this request as push message.
	

	if (this.getXmlMAPDialog() != null)
		this.processReceivedMAPEvent((MAPEvent) event);
	else {
		/*   WE DONT WNT TO SEND ANY THING BY NOW
		try {

		    long invokeId = event.getInvokeId();
	            MAPDialogSupplementary mapDialog = event.getMAPDialog();
		    CBSDataCodingScheme cbsDataCodingScheme = new CBSDataCodingSchemeImpl(0x0f);
	            //mapDialogMobility.setUserObject(invokeId);
		// instead use CMP  this.setIAnyTimeInterrogationRequestInvokeId(event.getInvokeId());

	            MAPParameterFactoryImpl mapFactory = new MAPParameterFactoryImpl();

		    USSDString ussdString = mapFactory.createUSSDString("1", cbsDataCodingScheme, null);
		    mapDialog.addUnstructuredSSResponse(event.getInvokeId(), cbsDataCodingScheme, ussdString);
		    mapDialog.send();

		} catch (MAPException mapException) {
			 logger.severe("MAP Exception while processing UnstructuredSSRequest ", mapException);
		} catch (Exception e) {
			  logger.severe("Exception while processing UnstructuredSSRequest ", e);
		}
		*/
	}


}

	// gateway has responded with menu
	// this end is going to send single choice of menu
public void onUnstructuredSSNotifyRequest(UnstructuredSSNotifyRequest event, ActivityContextInterface aci,
	EventContext eventContext) 
{
//  MLPResponse.MLPResultType result;

	try {
		//	response.text= event.getUSSDString().getString(null);
		if (logger.isInfoEnabled()) {
			logger.info("Rx UnstructuredSSNotifyRequest Indication. USSD String=" + event.getUSSDString().getString(null));	
		}
		long invokeId = event.getInvokeId();
		MAPDialogSupplementary dialog = event.getMAPDialog();
		dialog.addUnstructuredSSNotifyResponse(invokeId);
		dialog.close(false);
		} catch (MAPException e) {
		e.printStackTrace();
	}

}



 public void onMTForwardShortMessageRequest(
		 org.mobicents.protocols.ss7.map.api.service.sms.MtForwardShortMessageRequest event,
		 ActivityContextInterface aci /*, EventContext eventContext*/ ) {
		logger.info("onMTForwardShortMessageRequest");
	 
		MAPDialogSms mapDialogSms = event.getMAPDialog();
		
		logger.info(event.toString());
	//	 this.mapAcif.getActivityContextInterface(mapDialogSms).attach(this.sbbContext.getSbbLocalObject());
		 
		try {
			mapDialogSms.addMtForwardShortMessageResponse(event.getInvokeId(), null, null);
			mapDialogSms.close(false);
		} catch (MAPException e) {
			// TODO Auto-generated catch block
			logger.severe("Exception in sending MTForwardShortMessageResponse", e);
		}
	//	mapDialogSms.setUserObject(true);
	//	this.setDialog(mapDialogSms);
 }


    /**
     * Handle HTTP POST request
     * @param event
     * @param aci
     * @param eventContext
     */

	// this method is used to http post requests for MS-initiated USSD REQUESTS
    public void onPost(net.java.slee.resource.http.events.HttpServletRequestEvent event, 
    		ActivityContextInterface aci, EventContext eventContext) {

	logger.info("onPost");
      HttpServletRequest httpServletRequest = event.getRequest();

 //	System.out.println(httpServletRequest.getPathInfo());


     HttpRequestType httpRequestType = HttpRequestType.fromPath(httpServletRequest.getPathInfo());


//	logger.info("httpRequestType="+ httpRequestType);

	switch(httpRequestType) {
		case REST:
				break;
		default:
			return;
      }


        setEventContextCMP(eventContext);
 
	ISDNAddressString msisdn = null;
	String serviceCode = null;
	SccpAddress origAddress= null;
	SccpAddress destAddress= null;


	XmlMAPDialog xmlMAPDialog = null;


	try {


	eventContext.suspendDelivery(EVENT_SUSPEND_TIMEOUT);
	xmlMAPDialog = deserializeDialog(event);
	this.setXmlMAPDialog(xmlMAPDialog);

	final FastList<MAPMessage> mapMessages = xmlMAPDialog.getMAPMessages();

	logger.info("mapMessages="+mapMessages);

	if (mapMessages != null) {
		for (FastList.Node<MAPMessage> n = mapMessages.head(), end = mapMessages.tail(); (n = n.getNext()) != end;) {
				final MAPMessage rawMessage = n.getValue();
				final MAPMessageType type = rawMessage.getMessageType();

				if (logger.isInfoEnabled())
					logger.info("Dialog message type: " + type);

				switch (type) {
						/*
				case unstructuredSSRequest_Request:
					final UnstructuredSSRequest ussRequest = (UnstructuredSSRequest) rawMessage;
					msisdn = ussRequest.getMSISDNAddressString();
					serviceCode = ussRequest.getUSSDString().getString(null);
					break;
				case unstructuredSSNotify_Request:
					final UnstructuredSSNotifyRequest ntfyReq = (UnstructuredSSNotifyRequest) rawMessage;
					msisdn = ntfyReq.getMSISDNAddressString();
					serviceCode = ntfyReq.getUSSDString().getString(null);
					break;
						*/
				case unstructuredSSRequest_Response:
					final UnstructuredSSResponse unstrSSRes = (UnstructuredSSResponse) rawMessage;
					logger.info("unhandled unstructedSSRequest_Response");
					origAddress = xmlMAPDialog.getLocalAddress();
					logger.info("origAddress="+origAddress);
					destAddress = xmlMAPDialog.getRemoteAddress();
					logger.info("destAddress="+destAddress);
	

					break;
				case processUnstructuredSSRequest_Request:
					final ProcessUnstructuredSSRequest processUnstrSSReq = (ProcessUnstructuredSSRequest) rawMessage;
					msisdn = processUnstrSSReq.getMSISDNAddressString();
					serviceCode = processUnstrSSReq.getUSSDString().getString(null);
					logger.info("serviceCode="+serviceCode);
					msisdn = processUnstrSSReq.getMSISDNAddressString();
					logger.info("msisdn="+msisdn);
					origAddress = xmlMAPDialog.getLocalAddress();
					logger.info("origAddress="+origAddress);
					destAddress = xmlMAPDialog.getRemoteAddress();
					logger.info("destAddress="+destAddress);
					break;
				}
			}// for
		}




	HttpSession httpSession = event.getRequest().getSession(true);
	HttpSessionActivity httpSessionActivity = this.httpServletProvider.getHttpSessionActivity(httpSession);
	ActivityContextInterface httpSessionActivityContextInterface = httpServletRaActivityContextInterfaceFactory
	.getActivityContextInterface(httpSessionActivity);
	httpSessionActivityContextInterface.attach(this.sbbContext.getSbbLocalObject());

	} catch (Exception e) {
	this.logger.severe("Error while processing received HTTP POST request", e);
	}

       // handleLocationResponse( null, "Invalid MSISDN specified");


// TODO: org/dest refs may be null?
 	// this is GW sccp address
//	final SccpAddress origAddress = getUssdGwSccpAddress(xmlMAPDialog.getNetworkId());

//	final AddressString origReference = getUssdGwReference(xmlMAPDialog.getNetworkId());
	// this is VLR/MSC address
//	final SccpAddress destAddress = getMSCSccpAddress();
	// Table of 29.002 7.3/2
//	final AddressString destReference = getTargetReference();

	AddressString origReference = xmlMAPDialog.getReceivedOrigReference();//
	//this.mapProvider.getMAPParameterFactory()
	  //            .createAddressString(AddressNature.international_number, 
 //org.mobicents.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, "12345");
		  AddressString destReference = xmlMAPDialog.getReceivedDestReference();
		  //this.mapProvider.getMAPParameterFactory()
	        //      .createAddressString(AddressNature.international_number, 
 	//org.mobicents.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, "67890");

 //       GlobalTitle gt = sccpParameterFact.createGlobalTitle(address, 0, NumberingPlan.ISDN_TELEPHONY, null,
   //             NatureOfAddress.INTERNATIONAL);
//       	origAddress = sccpParameterFact.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, null, 2, 8);
//       	destAddress = sccpParameterFact.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, null, 1, 8);


	MAPDialogSupplementary mapDialog = null;

	try {
		if (logger.isInfoEnabled()) {
			logger.info("Creating dialog for, origAddress '" + origAddress + "', origReference '" + origReference
		+ "', destAddress '" + destAddress + "', destReference '" + destReference + "'");
			logger.fine("Map context '" + getUSSDMAPApplicationContext() + "'");
		}

		mapDialog = this.mapProvider.getMAPServiceSupplementary().createNewDialog(getUSSDMAPApplicationContext(),
					origAddress, origReference, destAddress, destReference);
		mapDialog.setReturnMessageOnError(xmlMAPDialog.getReturnMessageOnError());
		mapDialog.setNetworkId(xmlMAPDialog.getNetworkId());
			// mapDialog =
			// this.mapProvider.getMAPServiceSupplementary().createNewDialog(getUSSDMAPApplicationContext(),
			// origAddress, null, destAddress, null);

//	CBSDataCodingScheme cbs = new CBSDataCodingSchemeImpl(0x0f);
//	USSDString ussdString = this.mapProvider.getMAPParameterFactory().createUSSDString(serviceCode);
//	mapDialog.addProcessUnstructuredSSRequest(cbs, ussdString, null, msisdn);






		ActivityContextInterface mapDialogAci = this.mapAcif.getActivityContextInterface(mapDialog);
		mapDialogAci.attach(this.sbbContext.getSbbLocalObject());


     			// Lets do handshake only
//		mapDialog.send();
		pushToDevice(mapDialog);
	} catch (Exception e) {
	//		if (logger.isSevereEnabled())
			logger.severe("Failed to send USSD ProcessRequest!", e);
			if (mapDialog != null) {
				mapDialog.release();
            		}

//			try {

//				MAPUserAbortChoiceImpl abortChoice = new MAPUserAbortChoiceImpl();
//				abortChoice.setProcedureCancellationReason(ProcedureCancellationReason.associatedProcedureFailure);

//				xmlMAPDialog.reset();
//				xmlMAPDialog.abort(abortChoice);
//				xmlMAPDialog.setTCAPMessageType(MessageType.Abort);

//				this.abortHttpDialog(xmlMAPDialog);
//			} catch (Exception e1) {
//				logger.severe("Error while trying to send abort to HTTP App", e1);
//			}


		}






     //   onRequest(event, aci, eventContext);
    }


	public void onSessionPost(HttpServletRequestEvent event, ActivityContextInterface aci, EventContext eventContext) {
		if (logger.isInfoEnabled())
			logger.info("Received session POST");
 
		if (this.getEventContextCMP() != null) {
			//if (logger.isSevereEnabled())
			logger.info("Severe!!! Detected previous event context: " + getEventContextCMP());
			// TODO: send error
			return;
		}

        eventContext.suspendDelivery(EVENT_SUSPEND_TIMEOUT);
	this.setEventContextCMP(eventContext);
		// if this is new dialog, we need to send SRI
	boolean success = false;
	
	ISDNAddressString msisdn = null;
	String serviceCode = null;


	XmlMAPDialog xmlMAPDialog = null;


	try {


	xmlMAPDialog = deserializeDialog(event);


            byte[] data = getEventsSerializeFactory().serialize(xmlMAPDialog);

            logger.info("Got HTTP Response with Payload = \n" + new String(data));



	this.setXmlMAPDialog(xmlMAPDialog);

	final FastList<MAPMessage> mapMessages = xmlMAPDialog.getMAPMessages();

	logger.info("nmapMessages="+mapMessages);

	if (mapMessages != null) {
		for (FastList.Node<MAPMessage> n = mapMessages.head(), end = mapMessages.tail(); (n = n.getNext()) != end;) {
				final MAPMessage rawMessage = n.getValue();
				final MAPMessageType type = rawMessage.getMessageType();

				logger.info("Dialog message type: " + type);

				switch (type) {
				case unstructuredSSRequest_Request:
					final UnstructuredSSRequest ussRequest = (UnstructuredSSRequest) rawMessage;
					msisdn = ussRequest.getMSISDNAddressString();
					serviceCode = ussRequest.getUSSDString().getString(null);
					break;
				case unstructuredSSNotify_Request:
					final UnstructuredSSNotifyRequest ntfyReq = (UnstructuredSSNotifyRequest) rawMessage;
					msisdn = ntfyReq.getMSISDNAddressString();
					serviceCode = ntfyReq.getUSSDString().getString(null);
					break;
				case processUnstructuredSSRequest_Request:
					final ProcessUnstructuredSSRequest processUnstrSSReq = (ProcessUnstructuredSSRequest) rawMessage;
					msisdn = processUnstrSSReq.getMSISDNAddressString();
					serviceCode = processUnstrSSReq.getUSSDString().getString(null);
					break;
				}
			}// for
		}


	logger.info("new option="+serviceCode);

     	this.setXmlMAPDialog(xmlMAPDialog);
        pushToDevice();
        success = true;
     	// TODO: exceptions
      } catch (IOException e) {
		if (logger.isSevereEnabled())
			logger.severe("", e);
      } catch (XMLStreamException e) {
		if (logger.isSevereEnabled())
			logger.severe("", e);
      } catch (MAPException e) {
		if (logger.isSevereEnabled())
			logger.severe("", e);
      } 
   }

    /**
     * Handle HTTP GET request
     * @param event
     * @param aci
     * @param eventContext
     */
	public void onGet(net.java.slee.resource.http.events.HttpServletRequestEvent event, ActivityContextInterface aci,
			EventContext eventContext) {
	logger.info("onGet");
    //    onRequest(event, aci, eventContext);
	}

    /**
     * Entry point for all location lookups
     * Assigns a protocol handler to the request based on the path
     */
    private void onRequest(net.java.slee.resource.http.events.HttpServletRequestEvent event, ActivityContextInterface aci,
                           EventContext eventContext) {
        setEventContextCMP(eventContext);
        HttpServletRequest httpServletRequest = event.getRequest();
        HttpRequestType httpRequestType = HttpRequestType.fromPath(httpServletRequest.getPathInfo());

//	logger.info("httpServletRequest.getPathInfo()="+httpServletRequest.getPathInfo());
        setHttpRequest(new HttpRequest(httpRequestType));



	try {
	java.io.BufferedReader rd = httpServletRequest.getReader();
 	logger.info("rd=" + rd);
	
	String rline = "";
	  while ((rline = rd.readLine()) != null) {
        	logger.info(rline);
              } 
	} catch (Exception e) {
	  logger.severe("Exception while getting params ", e);
	}

	logger.info("request=" + httpServletRequest);
	logger.info("httpRequestType="+httpRequestType);
    String shortCode = null;
	logger.info("httpServletRequest.getQueryString()="+httpServletRequest.getQueryString());
        switch (httpRequestType) {
            case REST:
                shortCode = httpServletRequest.getParameter("code");
                break;
	    case OPT:
	//	requestingMSISDN = httpServletRequest.getParameter("option");
			break;
            default:
                //Silence not for you sendHTTPResult(HttpServletResponse.SC_NOT_FOUND, "Request URI unsupported");
                return;
        }

        setHttpRequest(new HttpRequest(httpRequestType, shortCode));
        if (logger.isInfoEnabled()){
            logger.info(String.format("Handling %s request, MSISDN: %s", httpRequestType.name().toUpperCase(), shortCode));
        }

        if (shortCode != null) {
	    switch(httpRequestType) {
		case REST:
	     //       eventContext.suspendDelivery();
	      //      getSingleMSISDNLocation(requestingMSISDN);
		    break;
		case OPT:
	    //        eventContext.suspendDelivery();
		 //   sendOptionRequest(requestingMSISDN);
			break;
	    }
        } else {
            logger.info("short code is null ");
            handleHttpErrorResponse("Invalid short code specified");
        }
    }

	/**
	 * CMP
	 */

	public abstract void setDialog(MAPDialog dialog);

	public abstract MAPDialog getDialog();

	public abstract void setXmlMAPDialog(XmlMAPDialog dialog);

	public abstract XmlMAPDialog getXmlMAPDialog();

	public abstract void setEventContextCMP(EventContext eventContext);

	public abstract EventContext getEventContextCMP();



    public abstract void setHttpRequest(HttpRequest httpRequest);

    public abstract HttpRequest getHttpRequest();

	public abstract void setUnstructuredSSRequest(UnstructuredSSRequest evt);
	public abstract UnstructuredSSRequest getUnstructuredSSRequest();

	/**
	 * Private helper methods
	 */

    private void sendOptionRequest(String option)
{
	UnstructuredSSRequest evt = this.getUnstructuredSSRequest();

	logger.info("evt="+ evt);
	if (evt == null)
		return;
		try {
			   long invokeId = evt.getInvokeId();
	            MAPDialogSupplementary mapDialog = evt.getMAPDialog();
		    CBSDataCodingScheme cbsDataCodingScheme = new CBSDataCodingSchemeImpl(0x0f);
	            //mapDialogMobility.setUserObject(invokeId);
		// instead use CMP  this.setIAnyTimeInterrogationRequestInvokeId(event.getInvokeId());

	            MAPParameterFactoryImpl mapFactory = new MAPParameterFactoryImpl();

		USSDString ussdString = mapFactory.createUSSDString("1", cbsDataCodingScheme, null);
			mapDialog.addUnstructuredSSResponse(evt.getInvokeId(), cbsDataCodingScheme, ussdString);
			mapDialog.send();



		} catch (MAPException mapException) {
			 logger.severe("MAP Exception while processing UnstructuredSSRequest ", mapException);
		} catch (Exception e) {
			  logger.severe("Exception while processing UnstructuredSSRequest ", e);
		}
}

    /**
     * Retrieve the location for the specified MSISDN via ATI request to the HLR
     */

	 /*
    private void getSingleMSISDNLocation(String requestingMSISDN) {
    
   try {


	  AddressString origRef = this.mapProvider.getMAPParameterFactory()
	              .createAddressString(AddressNature.international_number, 
 org.mobicents.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, "12345");
	      AddressString destRef = this.mapProvider.getMAPParameterFactory()
	              .createAddressString(AddressNature.international_number, 
 org.mobicents.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, "67890");

		SccpAddress origAddress;
		SccpAddress destAddress;
 //       GlobalTitle gt = sccpParameterFact.createGlobalTitle(address, 0, NumberingPlan.ISDN_TELEPHONY, null,
   //             NatureOfAddress.INTERNATIONAL);
        	origAddress = sccpParameterFact.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN,
		null, 2, 8);
        	destAddress = sccpParameterFact.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN,
		null, 1, 8);
        MAPDialogSupplementary mapDialog = this.mapProvider.getMAPServiceSupplementary().createNewDialog(
            MAPApplicationContext.getInstance(MAPApplicationContextName.networkUnstructuredSsContext,
MAPApplicationContextVersion.version2), origAddress, origRef, destAddress, destRef);

	CBSDataCodingScheme cbs = new CBSDataCodingSchemeImpl(0x0f);
	
		// USSD String: *125*+31628839999#
		// The Charset is null, here we let system use default Charset (UTF-7 as
		// explained in GSM 03.38. However if MAP User wants, it can set its own
		// impl of Charset
		USSDString ussdString = this.mapProvider.getMAPParameterFactory().createUSSDString(requestingMSISDN);
		 											
		ISDNAddressString msisdn = this.mapProvider.getMAPParameterFactory().createISDNAddressString(
		AddressNature.international_number, 
org.mobicents.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, "31628838002");
		mapDialog.addProcessUnstructuredSSRequest(cbs, ussdString, null, msisdn);

                ActivityContextInterface sriDialogACI = this.mapAcif.getActivityContextInterface(mapDialog);
                sriDialogACI.attach(this.sbbContext.getSbbLocalObject());
                mapDialog.send();
            } catch (MAPException e) {
                this.logger.severe("MAPException while trying to send ProcessUnstructuredSSRequest for MSISDN=" + requestingMSISDN, e);
                this.handleLocationResponse( null,
                        "System Failure: Failed to send request to network for position: " + e.getMessage());
            } catch (Exception e) {
                this.logger.severe("Exception while trying to send ATI request for MSISDN=" + requestingMSISDN, e);
                this.handleLocationResponse( null,
                        "System Failure: Failed to send request to network for position: " + e.getMessage());
            }
        

      }
*/


	protected SccpAddress getGmlcSccpAddress() {
		if (this.gmlcSCCPAddress == null) {
            GlobalTitle gt = sccpParameterFact.createGlobalTitle(mscPropertiesManagement.getMscGt(), 0,
                    NumberingPlan.ISDN_TELEPHONY, null, NatureOfAddress.INTERNATIONAL);
            this.gmlcSCCPAddress = sccpParameterFact.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE,
                    gt, 0, mscPropertiesManagement.getGmlcSsn());

//			GlobalTitle0100 gt = new GlobalTitle0100Impl(gmlcPropertiesManagement.getGmlcGt(),0,BCDEvenEncodingScheme.INSTANCE,NumberingPlan.ISDN_TELEPHONY,NatureOfAddress.INTERNATIONAL);
//			this.serviceCenterSCCPAddress = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, 0, gmlcPropertiesManagement.getGmlcSsn());
		}
		return this.gmlcSCCPAddress;
	}

	private MAPApplicationContext getSRIMAPApplicationContext() {
		if (this.anyTimeEnquiryContext == null) {
			this.anyTimeEnquiryContext = MAPApplicationContext.getInstance(
					MAPApplicationContextName.anyTimeEnquiryContext, MAPApplicationContextVersion.version3);
		}
		return this.anyTimeEnquiryContext;
	}

	private SccpAddress getHlrSCCPAddress(String address) {
        GlobalTitle gt = sccpParameterFact.createGlobalTitle(address, 0, NumberingPlan.ISDN_TELEPHONY, null,
                NatureOfAddress.INTERNATIONAL);
        return sccpParameterFact.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, 0,
                mscPropertiesManagement.getHlrSsn());

//	    GlobalTitle0100 gt = new GlobalTitle0100Impl(address, 0, BCDEvenEncodingScheme.INSTANCE,NumberingPlan.ISDN_TELEPHONY, NatureOfAddress.INTERNATIONAL);
//		return new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, 0, gmlcPropertiesManagement.getHlrSsn());
	}

    /**
     * Handle generating the appropriate HTTP response
     * We're making use of the MLPResponse class for both GET/POST requests for convenience and
     * because eventually the GET method will likely be removed
     * @param mlpResultType OK or error type to return to client
     * @param response CGIResponse on location attempt
     * @param mlpClientErrorMessage Error message to send to client
     */
	// Previously it was handleLocationResponse( CGIResponse response, String mlpClientErrorMessage)
    private void handleHttpErrorResponse( String error) {
        HttpRequest request = getHttpRequest();

        StringBuilder getResponse = new StringBuilder();

	if (error != null) {
		getResponse.append(error);
        this.sendHTTPResult(HttpServletResponse.SC_OK, getResponse.toString());	}
    }

    /**
     * Return the specified response data to the HTTP client
     * @param responseData Response data to send to client
     */
	private void sendHTTPResult(int statusCode, String responseData) {
		try {
			EventContext ctx = this.getEventContextCMP();
            if (ctx == null) {
                if (logger.isWarningEnabled()) {
                    logger.warning("When responding to HTTP no pending HTTP request is found, responseData=" + responseData);
                    return;
                }
            }

	        HttpServletRequestEvent event = (HttpServletRequestEvent) ctx.getEvent();

			HttpServletResponse response = event.getResponse();
                        response.setStatus(statusCode);
            PrintWriter w = null;
            w = response.getWriter();
            w.print(responseData);
			w.flush();
			response.flushBuffer();

			if (ctx.isSuspended()) {
				ctx.resumeDelivery();
			}

			if (logger.isInfoEnabled()){
			    logger.info("HTTP Request received and response sent, responseData=" + responseData);
			}

			// getNullActivity().endActivity();
		} catch (Exception e) {
			logger.severe("Error while sending back HTTP response", e);
		}
	}



    private void endHttpSessionActivity() {
        HttpSessionActivity httpSessionActivity = this.getHttpSessionActivity();
        if (httpSessionActivity != null) {
            httpSessionActivity.endActivity();
        }
    }

    private HttpSessionActivity getHttpSessionActivity() {
        ActivityContextInterface[] acis = this.sbbContext.getActivities();
        for (ActivityContextInterface aci : acis) {
            Object activity = aci.getActivity();
            if (activity instanceof HttpSessionActivity) {
                return (HttpSessionActivity) activity;
            }
        }
        return null;
    }




    private EventsSerializeFactory getEventsSerializeFactory() throws XMLStreamException {
        if (this.eventsSerializeFactory == null) {
            this.eventsSerializeFactory = new EventsSerializeFactory();
        }
        return this.eventsSerializeFactory;
    }


    /**
     * @param event
     * @return
     * @throws IOException
     * @throws XMLStreamException
     */
    private XmlMAPDialog deserializeDialog(HttpServletRequestEvent event) throws IOException, XMLStreamException {

        HttpServletRequest request = event.getRequest();
        if (!request.getContentType().equals(CONTENT_TYPE)) {
            throw new IOException("Wrong content type '" + request.getContentType() + "', should be '" + CONTENT_TYPE
                    + "'");
        }

        request.setCharacterEncoding("UTF-8");
        BufferedReader is = request.getReader();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        String line;
        Charset charset = Charset.forName("UTF-8");
        while ((line = is.readLine()) != null) {

            bos.write(line.getBytes(charset));
        }
        if (logger.isFinestEnabled()) {
            logger.info("Deserializing:" + request.getContentType() + ":" + request.getCharacterEncoding());
            logger.info(new String(bos.toByteArray()));
        }

        XmlMAPDialog d = getEventsSerializeFactory().deserialize(bos.toByteArray());

        return d;
    }


	private MAPApplicationContext getUSSDMAPApplicationContext() throws MAPException {
		MAPApplicationContext ctx = null;//this.getMAPApplicationContextCMP();
		if (ctx == null) {
			ctx = MAPApplicationContext.getInstance(MAPApplicationContextName.networkUnstructuredSsContext,
					MAPApplicationContextVersion.version2);
			if (ctx == null) {
				throw new MAPException("Not suitable context: "
						+ MAPApplicationContextName.networkUnstructuredSsContext + " for "
						+ "null"/*this.getMaxMAPApplicationContextVersionCMP()*/);
			}
		//	this.setMAPApplicationContextCMP(ctx);
		}
		return ctx;
	}


  protected MAPDialogSupplementary getMAPDialog() {
		MAPDialogSupplementary mapDialog = null;

		ActivityContextInterface[] acis = this.sbbContext.getActivities();
		for (ActivityContextInterface aci : acis) {
			Object activity = aci.getActivity();
			if (activity instanceof MAPDialogSupplementary) {
				return (MAPDialogSupplementary) activity;
			}
		}

		return mapDialog;
	}


   private void pushToDevice() throws MAPException {

	logger.info("pushToDevice()");
        MAPDialogSupplementary dialog = this.getMAPDialog();
        if (dialog == null) {
            throw new MAPException("Underlying MAP Dialog is null");
        }
        this.pushToDevice(dialog);
    }

    /**
     * @throws MAPException
     * 
     */
    private void pushToDevice(MAPDialogSupplementary dialog) throws MAPException {
      //  if (logger.isFinestEnabled())
        logger.info("PushToDevice " + dialog.toString());

        XmlMAPDialog xmlMAPDialog = this.getXmlMAPDialog();

        MAPUserAbortChoice mapUserAbortReason = xmlMAPDialog.getMAPUserAbortChoice();
        if (mapUserAbortReason != null) {
            dialog.abort(mapUserAbortReason);
            this.resumeHttpEventContext();
            this.endHttpSessionActivity();
            return;
        }

        Boolean prearrangedEnd = xmlMAPDialog.getPrearrangedEnd();

        this.processXmlMAPDialog(xmlMAPDialog, dialog);

        if (prearrangedEnd != null) {
            dialog.close(prearrangedEnd);

            // If prearrangedEnd is not null means, no more MAP messages are
            // expected. Lets clear HTTP resources
            this.resumeHttpEventContext();
            this.endHttpSessionActivity();
        } else {
            dialog.send();
        }
    }


protected void processXmlMAPDialog(XmlMAPDialog xmlMAPDialog, MAPDialogSupplementary mapDialog)
			throws MAPException {
        FastList<MAPMessage> mapMessages = xmlMAPDialog.getMAPMessages();
        if (mapMessages != null) {
            for (FastList.Node<MAPMessage> n = mapMessages.head(), end = mapMessages.tail(); (n = n.getNext()) != end;) {
                Long invokeId = this.processMAPMessageFromApplication(n.getValue(), mapDialog, xmlMAPDialog.getCustomInvokeTimeOut());
		logger.info("\n new invokeId:"+ invokeId + " message=" + n.getValue());
            }
        }
	}



	protected Long processMAPMessageFromApplication(MAPMessage mapMessage,
			MAPDialogSupplementary mapDialogSupplementary, Integer customInvokeTimeout) throws MAPException {
		switch (mapMessage.getMessageType()) {
		case unstructuredSSRequest_Request:
//            this.ussdStatAggregator.updateUssdRequestOperations();
			UnstructuredSSRequest unstructuredSSRequest = (UnstructuredSSRequest) mapMessage;
			if (customInvokeTimeout != null) {
				return mapDialogSupplementary.addUnstructuredSSRequest(customInvokeTimeout,
						unstructuredSSRequest.getDataCodingScheme(), unstructuredSSRequest.getUSSDString(),
						unstructuredSSRequest.getAlertingPattern(), unstructuredSSRequest.getMSISDNAddressString());
			}
			return mapDialogSupplementary.addUnstructuredSSRequest(unstructuredSSRequest.getDataCodingScheme(),
					unstructuredSSRequest.getUSSDString(), unstructuredSSRequest.getAlertingPattern(),
					unstructuredSSRequest.getMSISDNAddressString());
		case unstructuredSSRequest_Response:
			UnstructuredSSResponse unstructuredSSResponse = (UnstructuredSSResponse) mapMessage;
			logger.info("Sending UnstructuredSSResponse with invokeId="+unstructuredSSResponse.getInvokeId());
			// getting invoke id from userobject
	//		logger.info("We have got userObject=" + userObject);
			mapDialogSupplementary.addUnstructuredSSResponse(unstructuredSSResponse.getInvokeId(),
					unstructuredSSResponse.getDataCodingScheme(), unstructuredSSResponse.getUSSDString());
	//		mapDialogSupplementary.addUnstructuredSSResponse(Long.parseLong(userObject),
	//				unstructuredSSResponse.getDataCodingScheme(), unstructuredSSResponse.getUSSDString());
	
			break;

		case processUnstructuredSSRequest_Response:
			ProcessUnstructuredSSResponse processUnstructuredSSResponse = (ProcessUnstructuredSSResponse) mapMessage;
			mapDialogSupplementary.addProcessUnstructuredSSResponse(processUnstructuredSSResponse.getInvokeId(),
					processUnstructuredSSResponse.getDataCodingScheme(), processUnstructuredSSResponse.getUSSDString());
			return processUnstructuredSSResponse.getInvokeId();
		case unstructuredSSNotify_Request:
//            this.ussdStatAggregator.updateUssdNotifyOperations();
			// notify, this means dialog will end;
			final UnstructuredSSNotifyRequest ntfyRequest = (UnstructuredSSNotifyRequest) mapMessage;
			if (customInvokeTimeout != null) {
				return mapDialogSupplementary.addUnstructuredSSNotifyRequest(customInvokeTimeout,
						ntfyRequest.getDataCodingScheme(), ntfyRequest.getUSSDString(),
						ntfyRequest.getAlertingPattern(), ntfyRequest.getMSISDNAddressString());
			}
			return mapDialogSupplementary
					.addUnstructuredSSNotifyRequest(ntfyRequest.getDataCodingScheme(), ntfyRequest.getUSSDString(),
							ntfyRequest.getAlertingPattern(), ntfyRequest.getMSISDNAddressString());
		case unstructuredSSNotify_Response:
			// notify, this means dialog will end;
			final UnstructuredSSNotifyResponse ntfyResponse = (UnstructuredSSNotifyResponse) mapMessage;
			mapDialogSupplementary.addUnstructuredSSNotifyResponse(ntfyResponse.getInvokeId());
			break;
		case processUnstructuredSSRequest_Request:
//            this.ussdStatAggregator.updateProcessUssdRequestOperations();
			ProcessUnstructuredSSRequest processUnstructuredSSRequest = (ProcessUnstructuredSSRequest) mapMessage;
			if (customInvokeTimeout != null) {
				return mapDialogSupplementary.addProcessUnstructuredSSRequest(customInvokeTimeout,
						processUnstructuredSSRequest.getDataCodingScheme(),
						processUnstructuredSSRequest.getUSSDString(),
						processUnstructuredSSRequest.getAlertingPattern(),
						processUnstructuredSSRequest.getMSISDNAddressString());
			}
			return mapDialogSupplementary.addProcessUnstructuredSSRequest(
					processUnstructuredSSRequest.getDataCodingScheme(), processUnstructuredSSRequest.getUSSDString(),
					processUnstructuredSSRequest.getAlertingPattern(),
					processUnstructuredSSRequest.getMSISDNAddressString());

		}// switch

		return null;
	}


  private EventContext resumeHttpEventContext() {
        EventContext httpEventContext = getEventContextCMP();

        if (httpEventContext == null) {
            logger.severe("No HTTP event context, can not resume ");
            return null;
        }

        httpEventContext.resumeDelivery();
        return httpEventContext;
    }

private void processReceivedMAPEvent(MAPEvent event) {
	
	logger.info("processReceivedMAPEvent");
	XmlMAPDialog dialog = this.getXmlMAPDialog();
	if (dialog != null) {
        	dialog.reset();
        	dialog.addMAPMessage(event.getWrappedEvent());
	}

        MessageType messageType = null;
	messageType = event.getMAPDialog().getTCAPMessageType();
	if (dialog != null) {
        	dialog.setTCAPMessageType(messageType);
        	setXmlMAPDialog(dialog);
	}

    sendHttpResponse();

    if (messageType == MessageType.End) {
            // If MAP Dialog is end, lets kill HTTP Session Activity too
            this.endHttpSessionActivity();
    }
}

 private void sendHttpResponse() {

	logger.info("About to send HTTP response.");

    try {

            XmlMAPDialog dialog = getXmlMAPDialog();
            byte[] data = getEventsSerializeFactory().serialize(dialog);
            logger.info("Sending HTTP Response Payload = \n" + new String(data));
            EventContext httpEventContext = this.resumeHttpEventContext();

            if (httpEventContext == null) {
                 // TODO: terminate dialog?
                logger.severe("No HTTP event context, can not deliver response for MapXmlDialog: " + dialog);
                return;
            }

            HttpServletRequestEvent httpRequest = (HttpServletRequestEvent) httpEventContext.getEvent();
            HttpServletResponse response = httpRequest.getResponse();
            response.setStatus(HttpServletResponse.SC_OK);
            try {
                response.getOutputStream().write(data);
                response.getOutputStream().flush();
            } catch (NullPointerException npe) {
                logger.warning(  "Probably HTTPResponse already sent by HTTP-Servlet-RA. Increase HTTP_REQUEST_TIMEOUT in deploy-config.xml of RA to be greater than TCAP Dialog timeout", npe);
            }

    } catch (XMLStreamException xmle) {
            logger.severe("Failed to serialize dialog", xmle);
    } catch (IOException e) {
            logger.severe("Failed to send answer!", e);
    }

 }

 private void abortHttpDialog(XmlMAPDialog dialog) {

	logger.info("About to send HTTP abort dilog.");
	this.setXmlMAPDialog(dialog);

    try {
            byte[] data = getEventsSerializeFactory().serialize(dialog);
            logger.info("Sending HTTP Response Payload = \n" + new String(data));
            EventContext httpEventContext = this.resumeHttpEventContext();

            if (httpEventContext == null) {
                 // TODO: terminate dialog?
                logger.severe("No HTTP event context, can not deliver response for MapXmlDialog: " + dialog);
                return;
            }

            HttpServletRequestEvent httpRequest = (HttpServletRequestEvent) httpEventContext.getEvent();
            HttpServletResponse response = httpRequest.getResponse();
            response.setStatus(HttpServletResponse.SC_OK);
            try {
                response.getOutputStream().write(data);
                response.getOutputStream().flush();
            } catch (NullPointerException npe) {
                logger.warning(  "Probably HTTPResponse already sent by HTTP-Servlet-RA. Increase HTTP_REQUEST_TIMEOUT in deploy-config.xml of RA to be greater than TCAP Dialog timeout", npe);
			}
			// sendHttpResponse();
			this.endHttpSessionActivity();

    } catch (XMLStreamException xmle) {
            logger.severe("Failed to serialize dialog", xmle);
    } catch (IOException e) {
            logger.severe("Failed to send answer!", e);
    }

 }

           

}
