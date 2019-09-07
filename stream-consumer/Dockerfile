FROM python:3

COPY . /source
WORKDIR /source

RUN pip install pipenv
RUN pipenv install --deploy --system --ignore-pipfile

ENTRYPOINT ["python", "--", "/source/receive.py"]
