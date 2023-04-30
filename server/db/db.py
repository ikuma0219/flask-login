from sqlalchemy import create_engine
from sqlalchemy.orm import scoped_session, sessionmaker
from models import Base

def new_database(user, password, host, db_name):
    DATABASE = f'mysql+mysqlconnector://{user}:{password}@{host}/{db_name}'
    engine = create_engine(DATABASE)
    Base.metadata.create_all(bind=engine)
    session = scoped_session(
        sessionmaker(
            autocommit=False,
            autoflush=False,
            bind=engine
        )
    )
    return session