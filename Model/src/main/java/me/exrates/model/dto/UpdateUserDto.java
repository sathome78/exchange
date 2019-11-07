package me.exrates.model.dto;

import lombok.Data;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;

/**
 * this DTO is for update user and consists modifiable fields only
 */
@Data
public class UpdateUserDto {
	private int id;
	private String email;
	private String phone;
	private UserStatus status;
	private String password;
	private String finpassword;
	private UserRole role;
	private Boolean verificationRequired;
	private Boolean tradesPrivileges;
	private String publicId;

	/*constructors*/
	public UpdateUserDto(int id) {
		this.id = id;
	}
}
