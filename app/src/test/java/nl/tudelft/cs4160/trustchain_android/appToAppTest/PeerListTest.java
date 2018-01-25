package nl.tudelft.cs4160.trustchain_android.appToAppTest;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import nl.tudelft.cs4160.trustchain_android.appToApp.PeerAppToApp;
import nl.tudelft.cs4160.trustchain_android.appToApp.PeerList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.when;

/**
 * Created by Boning on 12/17/2017.
 */

public class PeerListTest {
    private PeerList peerlist;
    private ArrayList<PeerAppToApp> originalIpList;
    private ArrayList<PeerAppToApp> expectedIpList;
    InetSocketAddress rnadomInet = new InetSocketAddress(200);

    @Before
    public void initialization(){
        PeerAppToApp peer1 = new PeerAppToApp("peer1", rnadomInet);
        PeerAppToApp peer2 = new PeerAppToApp("peer2", rnadomInet);
        PeerAppToApp peer3 = new PeerAppToApp("peer3", rnadomInet);
//        when(inetMock.equals(any(InetSocketAddress.class))).thenReturn(true);

        originalIpList = new ArrayList<PeerAppToApp>();
        originalIpList.add(peer1);
        originalIpList.add(peer2);
        originalIpList.add(peer3);
        originalIpList.add(peer1);

        expectedIpList = new ArrayList<PeerAppToApp>();
        expectedIpList.add(peer1);
        expectedIpList.add(peer2);
        expectedIpList.add(peer3);

        peerlist = new PeerList(originalIpList);
    }

    @Test
    public void removeDuplicatesTest(){
        peerlist.removeDuplicates();
        ArrayList<PeerAppToApp> newIPPeerList = peerlist.getList();
        boolean failed = false;

        for (PeerAppToApp peer: newIPPeerList) {
            if(!expectedIpList.remove(peer)){
                failed = true;
                break;
            }
        }

        if(expectedIpList.size() != 0) failed=false;
        assertFalse(failed);

    }

    @Test
    public void peerExistsInListTest(){
        PeerAppToApp peer4 = new PeerAppToApp("peer4", rnadomInet);
        assertTrue(peerlist.peerExistsInList(originalIpList.get(0)));
        assertFalse(peerlist.peerExistsInList(peer4));
    }

    @Test
    public void testCertainMethods() {
        peerlist = new PeerList();
        PeerAppToApp peer = new PeerAppToApp("peer", rnadomInet);
        peerlist.add(peer);
        assertEquals(1, peerlist.size());
        peerlist.remove(peer);
        assertEquals(0, peerlist.size());
    }

    @After
    public void resetMocks() {
        validateMockitoUsage();
    }
}
