package co.edu.cesde.pps.application;

import co.edu.cesde.pps.dto.AddressDTO;
import co.edu.cesde.pps.model.User;
import co.edu.cesde.pps.service.AddressService;
import co.edu.cesde.pps.service.UserSessionService;
import co.edu.cesde.pps.web.dto.request.AddressUpsertRequest;
import co.edu.cesde.pps.web.dto.response.AddressResponse;
import co.edu.cesde.pps.web.mapper.WebRequestMapper;
import co.edu.cesde.pps.web.mapper.WebResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AddressApplicationService {

    private final UserSessionService userSessionService;
    private final AddressService addressService;
    private final WebRequestMapper requestMapper;
    private final WebResponseMapper responseMapper;

    public AddressApplicationService(UserSessionService userSessionService,
                                     AddressService addressService,
                                     WebRequestMapper requestMapper,
                                     WebResponseMapper responseMapper) {
        this.userSessionService = userSessionService;
        this.addressService = addressService;
        this.requestMapper = requestMapper;
        this.responseMapper = responseMapper;
    }

    public List<AddressResponse> listCurrentUserAddresses(String token) {
        User user = userSessionService.requireAuthenticatedUser(token);
        return responseMapper.toAddressResponses(addressService.findUserAddresses(user.getUserId()));
    }

    @Transactional
    public AddressResponse addAddress(String token, AddressUpsertRequest request) {
        User user = userSessionService.requireAuthenticatedUser(token);
        AddressDTO dto = addressService.addAddress(user.getUserId(), requestMapper.toAddressDTO(request));
        return responseMapper.toAddressResponse(dto);
    }

    @Transactional
    public AddressResponse updateAddress(String token, Long addressId, AddressUpsertRequest request) {
        userSessionService.requireAuthenticatedUser(token);
        AddressDTO dto = addressService.updateAddress(addressId, requestMapper.toAddressDTO(request));
        return responseMapper.toAddressResponse(dto);
    }

    @Transactional
    public void deleteAddress(String token, Long addressId) {
        User user = userSessionService.requireAuthenticatedUser(token);
        addressService.deleteAddress(user.getUserId(), addressId);
    }

    @Transactional
    public AddressResponse setDefaultAddress(String token, Long addressId) {
        User user = userSessionService.requireAuthenticatedUser(token);
        return responseMapper.toAddressResponse(
                addressService.setDefaultAddress(user.getUserId(), addressId));
    }
}
