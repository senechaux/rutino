package com.senechaux.rutino.db.entities;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;

//@DatabaseTable
public abstract class BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String ID = "_id";

	@DatabaseField(generatedId = true)
	protected Integer _id;

	public Integer get_id() {
		return _id;
	}

	public void set_id(Integer _id) {
		this._id = _id;
	}

	@Override
	public int hashCode() {
		return _id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseEntity other = (BaseEntity) obj;
		if (_id == null) {
			if (other.get_id() != null)
				return false;
		} else if (!_id.equals(other.get_id()))
			return false;
		return true;
	}

}
